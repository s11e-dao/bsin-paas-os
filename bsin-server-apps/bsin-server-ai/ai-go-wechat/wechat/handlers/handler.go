package handlers

import (
	log "bsinpass/go/pkg/logging"
	"fmt"
	"runtime"
	"strings"
	"time"

	config "bsinpass/go/conf"
	"bsinpass/go/middleware/redis"
	"unicode/utf8"

	"github.com/eatmoreapple/openwechat"
	"github.com/patrickmn/go-cache"
	"github.com/skip2/go-qrcode"
)

var LocalCache = cache.New(config.WechatSetting.SessionTimeout, time.Minute*5)

// MessageHandlerInterface 消息处理接口
type MessageHandlerInterface interface {
	handle() error
	ReplyText() error
}

// QrCodeCallBack 登录扫码回调，
func QrCodeCallBack(uuid string) {
	if runtime.GOOS == "windows" {
		// 运行在Windows系统上
		openwechat.PrintlnQrcodeUrl(uuid)
	} else {
		log.Info("login in linux: " + uuid)
		q, _ := qrcode.New("https://login.weixin.qq.com/l/"+uuid, qrcode.Low)
		// log.Info("如果二维码无法扫描，请缩小控制台尺寸，或更换命令行工具，缩小二维码像素")
		// 打印二维码在控制台
		log.Info(q.ToString(true))
		// TODO: 待优化，最多20s登录一个
		var cycle = 0
		for {
			uuid, err := redis.Get("uuid")
			log.Info("exist uuid:", uuid, err)
			if err != nil {
				break
			}
			log.Warn("wait lasr login uuid: ", uuid)
			time.Sleep(time.Second * 2)
			cycle++
			if cycle > 11 {
				log.Error("why i an here?, redis error???")
				break
			}
		}
		err := redis.Set("uuid", uuid, time.Second*100)
		if err != nil {
			log.Error(err)
		}
		loginQRurl := openwechat.GetQrcodeUrl(uuid)
		err = redis.Set(uuid+"-loginQrUrl:", loginQRurl, time.Second*100)
		if err != nil {
			log.Error(err)
		}
	}
}

// LoginCallBack 登录回调
func LoginCallBack(_ openwechat.CheckLoginResponse) {
	log.Info("登录成功")
}

// ScanCallBack 扫码回调
func ScanCallBack(_ openwechat.CheckLoginResponse) {
	log.Info("扫码成功,请在手机上确认登录")
}

// SyncCheckCallback 心跳回调函数
func SyncCheckCallback(resp openwechat.SyncCheckResponse) {
	log.Info("RetCode:", resp.RetCode, "Selector:", resp.Selector)
}

func isTokenMessage(msg string) bool {
	ret := strings.Contains(msg, config.WechatSetting.SessionClearToken)
	ret = ret && utf8.RuneCountInString(msg) < 20
	return ret
}

func NewHandler() (msgFunc func(msg *openwechat.Message), err error) {
	dispatcher := openwechat.NewMessageMatchDispatcher()

	// 消息过滤处理
	dispatcher.RegisterHandler(func(message *openwechat.Message) bool {
		return isTokenMessage(message.Content)
	}, TokenMessageContextHandler())

	// 处理群消息
	dispatcher.RegisterHandler(func(message *openwechat.Message) bool {
		return message.IsSendByGroup()
	}, GroupMessageContextHandler())

	// 好友申请
	dispatcher.RegisterHandler(func(message *openwechat.Message) bool {
		return message.IsFriendAdd()
	}, func(ctx *openwechat.MessageContext) {
		msg := ctx.Message
		if config.WechatSetting.AutoPass {
			_, err := msg.Agree(config.WechatSetting.AutoPassResponse)
			if err != nil {
				log.Info(fmt.Sprintf("add friend agree error : %v", err))
				return
			}
		}
	})

	// 用户消息处理器
	dispatcher.RegisterHandler(func(message *openwechat.Message) bool {
		return !(strings.Contains(message.Content, config.WechatSetting.SessionClearToken) || message.IsSendByGroup() || message.IsFriendAdd())
	}, UserMessageContextHandler())

	return dispatcher.AsMessageHandler(), nil
}
