package handlers

import (
	"bsinpass/go/middleware/redis"
	log "bsinpass/go/pkg/logging"
	copilot "bsinpass/go/wechat/copilot"
	"bsinpass/go/wechat/service"
	"errors"
	"fmt"
	"strconv"
	"strings"

	"github.com/eatmoreapple/openwechat"
)

var _ MessageHandlerInterface = (*GroupMessageHandler)(nil)

// GroupMessageHandler 群消息处理
type GroupMessageHandler struct {
	// 获取自己
	self *openwechat.Self
	// 群
	group *openwechat.Group
	// 接收到消息
	msg *openwechat.Message
	// 发送的用户
	sender *openwechat.User
	// 实现的用户业务
	service service.UserServiceInterface
}

func GroupMessageContextHandler() func(ctx *openwechat.MessageContext) {
	return func(ctx *openwechat.MessageContext) {
		msg := ctx.Message
		// 获取用户消息处理器
		handler, err := NewGroupMessageHandler(msg)
		if err != nil {
			log.Info("init group message handler error: %s", err)
			return
		}

		// 处理用户消息
		err = handler.handle()
		if err != nil {
			log.Info("handle group message error: %s", err)
		}
	}
}

// NewGroupMessageHandler 创建群消息处理器
func NewGroupMessageHandler(msg *openwechat.Message) (MessageHandlerInterface, error) {
	sender, err := msg.Sender()
	receiver, err := msg.Receiver()
	if err != nil {
		return nil, err
	}
	group := &openwechat.Group{User: sender}
	groupSender, err := msg.SenderInGroup()
	if err != nil {
		return nil, err
	}

	userService := service.NewUserService(LocalCache, groupSender, receiver)
	handler := &GroupMessageHandler{
		self:    sender.Self(),
		msg:     msg,
		group:   group,
		sender:  groupSender,
		service: userService,
	}
	return handler, nil

}

// handle 处理消息
func (g *GroupMessageHandler) handle() error {
	wxNo := strconv.FormatInt(g.self.ID(), 10)
	log.Debug("group message handler, wxNo:", wxNo)
	groupChat, err := redis.Get("groupChatWithWxNo:" + wxNo)
	if err != nil {
		log.Error(err)
		return nil
	}
	if groupChat != "true" {
		log.Debug("该用户不支持群聊@功能！！！")
		return nil
	}

	if g.msg.IsText() {
		return g.ReplyText()
	}
	return nil
}

// ReplyText 发送文本消息到群
func (g *GroupMessageHandler) ReplyText() error {
	log.Info("Received Group %v Text Msg : %v", g.group.NickName, g.msg.Content)
	// 1.不是@的不处理
	if !g.msg.IsAt() {
		return nil
	}

	// 2.获取请求的文本，如果为空字符串不处理
	requestText := g.getRequestText()
	if requestText == "" {
		log.Info("user message is null")
		return nil
	}

	wxNo := strconv.FormatInt(g.self.Self().ID(), 10)
	log.Debug("receiver ID:", wxNo)
	copilotNo, err := redis.Get("copilotNoWithWxNo:" + wxNo)
	if err != nil {
		log.Info("not found userID:", wxNo, " copilot", err)
		exceptionResp, _ := redis.Get("exceptionRespWithWxNo:" + wxNo)
		if err != nil {
			if exceptionResp == "true" {
				_, err = g.msg.ReplyText(g.buildReplyText("not found copilot"))
				if err != nil {
					return errors.New(fmt.Sprintf("response group error: %v ", err))
				}
			}
		}
		return nil
	}

	historyChatSummary, err := redis.Get("historyChatSummaryWithWxNo:" + wxNo)
	if err != nil {
		historyChatSummary = "false"
	}

	// 使用token请求
	bizParams := copilot.CopilotRequestBizParams{
		// TenantId:   "1737841274272223232",
		// MerchantNo: "1737853502828482561",
		CustomerNo:         "wxNo:" + wxNo, //微信聊天客户号:用于上下文存储
		CopilotNo:          copilotNo,
		Question:           requestText,
		HistoryChatSummary: historyChatSummary,
	}

	// 2.向Copilot发起请求
	reply, err := copilot.CopilotCompletions(bizParams)
	if err != nil {
		// 2.1 将请求失败信息输出给用户
		errMsg := fmt.Sprintf("copilot request error: %v", err)
		exceptionResp, err := redis.Get("exceptionRespWithWxNo:" + wxNo)
		if err != nil {
			if exceptionResp == "true" {
				_, err = g.msg.ReplyText(errMsg)
				if err != nil {
					return errors.New(fmt.Sprintf("response group error: %v ", err))
				}
			}
		}
		return err
	}

	// 4.响应信息给用户
	_, err = g.msg.ReplyText(g.buildReplyText(reply))
	if err != nil {
		return errors.New(fmt.Sprintf("response user error: %v ", err))
	}

	// 5.返回错误信息
	return err
}

// getRequestText 获取请求接口的文本，要做一些清洗
func (g *GroupMessageHandler) getRequestText() string {
	// 1.去除空格以及换行
	requestText := strings.TrimSpace(g.msg.Content)
	requestText = strings.Trim(g.msg.Content, "\n")

	// 2.替换掉当前用户名称
	replaceText := "@" + g.self.NickName
	requestText = strings.TrimSpace(strings.ReplaceAll(g.msg.Content, replaceText, ""))
	if requestText == "" {
		return ""
	}

	// 3.获取上下文，拼接在一起，如果字符长度超出4000，截取为4000。（GPT按字符长度算），达芬奇3最大为4068，也许后续为了适应要动态进行判断。
	sessionText := g.service.GetUserSessionContext()
	if sessionText != "" {
		requestText = sessionText + "\n" + requestText
	}
	if len(requestText) >= 4000 {
		requestText = requestText[:4000]
	}

	// 4.检查用户发送文本是否包含结束标点符号
	punctuation := ",.;!?，。！？、…"
	runeRequestText := []rune(requestText)
	lastChar := string(runeRequestText[len(runeRequestText)-1:])
	if strings.Index(punctuation, lastChar) < 0 {
		requestText = requestText + "？" // 判断最后字符是否加了标点，没有的话加上句号，避免openai自动补齐引起混乱。
	}

	// 5.返回请求文本
	return requestText
}

// buildReply 构建回复文本
func (g *GroupMessageHandler) buildReplyText(reply string) string {
	// 1.获取@我的用户
	atText := "@" + g.sender.NickName
	textSplit := strings.Split(reply, "\n\n")
	if len(textSplit) > 1 {
		trimText := textSplit[0]
		reply = strings.Trim(reply, trimText)
	}
	reply = strings.TrimSpace(reply)
	if reply == "" {
		return atText + " 请求得不到任何有意义的回复，请具体提出问题。"
	}

	// 2.拼接回复,@我的用户，问题，回复
	replaceText := "@" + g.self.NickName
	question := strings.TrimSpace(strings.ReplaceAll(g.msg.Content, replaceText, ""))
	reply = atText + "\n" + question + "\n --------------------------------\n" + reply
	reply = strings.Trim(reply, "\n")

	// 3.返回回复的内容
	return reply
}
