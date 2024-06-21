package handlers

import (
	"bsinpass/go/middleware/redis"
	copilot "bsinpass/go/wechat/copilot"
	"strconv"
	"time"

	log "bsinpass/go/pkg/logging"
	"bsinpass/go/wechat/service"
	"errors"
	"fmt"
	"strings"

	"github.com/eatmoreapple/openwechat"
)

var _ MessageHandlerInterface = (*UserMessageHandler)(nil)
var lastQyeryTime = time.Now()

// UserMessageHandler 私聊消息处理
type UserMessageHandler struct {
	// 接收到消息
	msg *openwechat.Message
	// 发送的用户
	sender *openwechat.User
	// 接收的用户
	receiver *openwechat.User
	// 实现的用户业务
	service service.UserServiceInterface
}

func UserMessageContextHandler() func(ctx *openwechat.MessageContext) {
	return func(ctx *openwechat.MessageContext) {
		msg := ctx.Message
		handler, err := NewUserMessageHandler(msg)
		if err != nil {
			log.Info(fmt.Sprintf("init user message handler error: %s", err))
			return
		}

		// 处理用户消息
		err = handler.handle()
		if err != nil {
			log.Info(fmt.Sprintf("handle user message error: %s", err))
		}
	}
}

// NewUserMessageHandler 创建私聊处理器
func NewUserMessageHandler(message *openwechat.Message) (MessageHandlerInterface, error) {
	sender, err := message.Sender()
	receiver, err := message.Receiver()
	if err != nil {
		return nil, err
	}
	userService := service.NewUserService(LocalCache, sender, receiver)
	handler := &UserMessageHandler{
		msg:      message,
		sender:   sender,
		receiver: receiver,
		service:  userService,
	}

	return handler, nil
}

// handle 处理消息
func (h *UserMessageHandler) handle() error {
	if h.msg.IsText() {
		return h.ReplyText()
	}
	return nil
}

// ReplyText 发送文本消息到群
func (h *UserMessageHandler) ReplyText() error {
	log.Debug("Received User %v Text Msg : %v", h.sender.NickName, h.msg.Content)
	if h.sender.Self().IsMP() {
		log.Debug("Received User %v is Mp", h.sender.NickName)
		return nil
	}
	wxNo := strconv.FormatInt(h.receiver.Self().ID(), 10)

	requestIntervalLimit, err := redis.Get("requestIntervalLimitWithWxNo:" + wxNo)
	if err != nil {
		requestIntervalLimit = "0"
	}
	tc := time.Since(lastQyeryTime) //计算耗时
	lastQyeryTime = time.Now()
	requestIntervalLimitInt, _ := strconv.Atoi(requestIntervalLimit)

	if requestIntervalLimitInt != 0 {
		if tc.Seconds() < (float64)(requestIntervalLimitInt) {
			return nil
		}
	}

	// 1.获取请求文本
	requestText := h.getRequestText()
	if requestText == "" {
		log.Warn("user message is null")
		return nil
	}
	log.Debug("receiver ID:", wxNo)
	copilotNo, err := redis.Get("copilotNoWithWxNo:" + wxNo)
	if err != nil {
		log.Info("not found userID:", wxNo, " copilot", err)
		exceptionResp, err := redis.Get("exceptionRespWithWxNo:" + wxNo)
		if err != nil {
			if exceptionResp == "true" {
				_, err = h.msg.ReplyText(buildUserReply("not found copilot", "ERROR:"))
				if err != nil {
					return errors.New(fmt.Sprintf("response user error: %v ", err))
				}
			}
		}
		return nil
	}
	preResp, err := redis.Get("preRespWithWxNo:" + wxNo)
	if err != nil {
		preResp = ""
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
				_, err = h.msg.ReplyText(errMsg)
				if err != nil {
					return errors.New(fmt.Sprintf("response user error: %v ", err))
				}
			}
		}
		return err
	}
	// 3.回复用户
	_, err = h.msg.ReplyText(buildUserReply(reply, preResp))
	if err != nil {
		return errors.New(fmt.Sprintf("response user error: %v ", err))
	}

	// 4.返回错误
	return err
}

// getRequestText 获取请求接口的文本
func (h *UserMessageHandler) getRequestText() string {
	// 1.去除空格以及换行
	requestText := strings.TrimSpace(h.msg.Content)
	requestText = strings.Trim(h.msg.Content, "\n")

	// // 2.检查用户发送文本是否包含结束标点符号
	// punctuation := ",.;!?，。！？、…"
	// runeRequestText := []rune(requestText)
	// lastChar := string(runeRequestText[len(runeRequestText)-1:])
	// if strings.Index(punctuation, lastChar) < 0 {
	// 	requestText = requestText + "?" // 判断最后字符是否加了标点，没有的话加上句号，避免openai自动补齐引起混乱。
	// }
	// 3.返回请求文本
	return requestText
}

// getRequestTextWithContex 获取请求接口的文本，加上上下文
func (h *UserMessageHandler) getRequestTextWithContex() string {
	// 1.去除空格以及换行
	requestText := strings.TrimSpace(h.msg.Content)
	requestText = strings.Trim(h.msg.Content, "\n")

	// 2.获取上下文，拼接在一起，如果字符长度超出4000，截取为4000。（GPT按字符长度算），达芬奇3最大为4068，也许后续为了适应要动态进行判断。
	sessionText := h.service.GetUserSessionContext()
	if sessionText != "" {
		requestText = sessionText + "\n" + requestText
	}
	if len(requestText) >= 4000 {
		requestText = requestText[:4000]
	}

	// 3.检查用户发送文本是否包含结束标点符号
	punctuation := ",.;!?，。！？、…"
	runeRequestText := []rune(requestText)
	lastChar := string(runeRequestText[len(runeRequestText)-1:])
	if strings.Index(punctuation, lastChar) < 0 {
		requestText = requestText + "？" // 判断最后字符是否加了标点，没有的话加上句号，避免openai自动补齐引起混乱。
	}

	// 4.返回请求文本
	return requestText
}

// 构建用户回复
func buildUserReply(reply string, preResp string) string {
	// 1.去除空格问号以及换行号，如果为空，返回一个默认值提醒用户
	textSplit := strings.Split(reply, "\n\n")
	if len(textSplit) > 1 {
		trimText := textSplit[0]
		reply = strings.Trim(reply, trimText)
	}
	reply = strings.TrimSpace(reply)

	reply = strings.TrimSpace(reply)
	if reply == "" {
		return "请求得不到任何有意义的回复，请具体提出问题。"
	}

	// 2.如果用户有配置前缀，加上前缀
	reply = preResp + "\n" + reply
	reply = strings.Trim(reply, "\n")

	// 3.返回拼接好的字符串
	return reply
}
