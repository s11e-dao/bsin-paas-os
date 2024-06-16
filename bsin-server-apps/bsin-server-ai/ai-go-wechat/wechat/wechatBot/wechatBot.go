package wechatBot

import (
	"bsinpass/go/middleware"
	_ "bsinpass/go/pkg/logging"
	log "bsinpass/go/pkg/logging"
	"bsinpass/go/wechat/handlers"
	"errors"
	"strconv"

	"bsinpass/go/middleware/redis"

	"time"

	"github.com/eatmoreapple/openwechat"
)

type WechatBotInfo struct {
	BoInfo middleware.WechatLoginInOutBizParams `json:"botInfo"`
	Bot    *openwechat.Bot                      `json:"bot"`
}

type WechatBotMonitorInfo struct {
	WxPlatformNo         string `json:"wxPlatformNo"`
	CopilotNo            string `json:"copilotNo"`
	RequestIntervalLimit string `json:"requestIntervalLimit"`
	PreResp              string `json:"preResp"`
	GroupChat            string `json:"groupChat"`
	HistoryChatSummary   string `json:"historyChatSummary"`
	ExpirationTime       string `json:"expirationTime"`
	ExceptionResp        string `json:"exceptionResp"`
	LoginIn              string `json:"loginIn"`
	Alive                bool   `json:"alive"`
	NickName             string `json:"nickname"`
}

/*
* 创建全局微信机器人集合
* key: WxPlatformNo
* value: WechatBotInfo
 */
var WechatBotInfoMap = make(map[string]WechatBotInfo)

func Run(accountLoginInBizParams middleware.WechatLoginInOutBizParams) *openwechat.Bot {
	// 1 Create wechatBot
	bot := openwechat.DefaultBot(openwechat.Desktop) // 桌面模式

	// 2 注册消息处理函数
	handler, err := handlers.NewHandler()
	if err != nil {
		log.Info("register error: %v", err)
		return nil
	}
	// 2.1 消息回调
	bot.MessageHandler = handler
	// 2.2 注册登陆二维码回调
	bot.UUIDCallback = handlers.QrCodeCallBack
	// 2.3 扫码回调
	bot.ScanCallBack = handlers.ScanCallBack
	// 2.4 登录回调
	bot.LoginCallBack = handlers.LoginCallBack
	// 2.5 心跳回调函数
	bot.SyncCheckCallback = handlers.SyncCheckCallback

	log.Info("登录中...")
	// 3 登陆：会阻塞
	if err := bot.Login(); err != nil {
		log.Info(err)
		return nil
	}
	log.Info("登录成功!!!")

	// 获取登陆的用户(登录成功后调用)
	self, err := bot.GetCurrentUser()
	if err != nil {
		log.Info(err)
		return nil
	}
	log.Info("登录用户 NickName:" + self.NickName)
	log.Info("登录用户 UserName:" + self.UserName)
	// 不同于UserName，ID是用户的唯一标识，且不会随着登录而改变

	wxNo := strconv.FormatInt(self.ID(), 10)
	log.Info("登录用户 唯一ID:", wxNo)
	uuid, err := redis.Get("uuid")

	loginStatus := "success"
	if err == nil {
		// 登录状态上报
		err = redis.Set(uuid+"-nickname:", self.NickName, time.Second*100)
		if err != nil {
			log.Error(err)
		}
		// 监控上报使用
		err = redis.Set("nickNameWithWxNo:"+wxNo, self.NickName, 0)
		if err != nil {
			log.Error(err)
		}
		// 登录状态上报
		err = redis.Set(uuid+"-wxPlatformNo:", accountLoginInBizParams.WxPlatformNo, time.Second*100)
		if err != nil {
			log.Error(err)
		}
		// 登录状态上报:一个wxPlatformNo唯一绑定一个微信
		err = redis.Set(uuid+"-wxNo:", wxNo, time.Second*100)
		if err != nil {
			log.Error(err)
		}

		// 消息回复使用
		err = redis.Set("wxPlatformNoWithWxNo:"+wxNo, accountLoginInBizParams.WxPlatformNo, 0)
		if err != nil {
			log.Error(err)
		}
		// 消息回复使用
		err = redis.Set("copilotNoWithWxNo:"+wxNo, accountLoginInBizParams.CopilotNo, 0)
		if err != nil {
			log.Error(err)
		}
		// 消息回复使用
		err = redis.Set("preRespWithWxNo:"+wxNo, accountLoginInBizParams.PreResp, 0)
		if err != nil {
			log.Error(err)
		}
		// 群聊消息支持判断
		err = redis.Set("groupChatWithWxNo:"+wxNo, accountLoginInBizParams.GroupChat, 0)
		if err != nil {
			log.Error(err)
		}
		// 对话总结
		err = redis.Set("historyChatSummaryWithWxNo:"+wxNo, accountLoginInBizParams.HistoryChatSummary, 0)
		if err != nil {
			log.Error(err)
		}
		// 服务过期判断使用
		err = redis.Set("expirationTimeWithWxNo:"+wxNo, accountLoginInBizParams.ExpirationTime, 0)
		if err != nil {
			log.Error(err)
		}

		// 接口异常回复
		err = redis.Set("exceptionRespWithWxNo:"+wxNo, accountLoginInBizParams.ExceptionResp, 0)
		if err != nil {
			log.Error(err)
		}

		// 消息回复限制使用
		err = redis.Set("requestIntervalLimitWithWxNo:"+wxNo, accountLoginInBizParams.RequestIntervalLimit, 0)
		if err != nil {
			log.Error(err)
		}
		wxPlatformToWxNoValue, err := redis.Get("wxNoWithWxPlatformNo:" + accountLoginInBizParams.WxPlatformNo)
		log.Info("wxPlatformToWxNo", wxPlatformToWxNoValue)
		// 首次登录，进行绑定操作
		if err != nil {
			// wxPlatformNo 和 wxNo 唯一绑定，一个wxPlatform只能绑定一个微信号
			err = redis.Set("wxNoWithWxPlatformNo:"+accountLoginInBizParams.WxPlatformNo, wxNo, 0)
			if err != nil {
				log.Error(err)
			}
		} else {
			// 已经存储，则校验二者是否相同，不同则退出
			if wxPlatformToWxNoValue != wxNo {
				log.Warn("所登录用户的ID与首次微信登录的ID不一致")
				loginStatus = "失败,登录微信号与绑定微信号不一致"
			}
		}
		accountLoginInBizParams.NickName = self.NickName
		wechatBotInfo := WechatBotInfo{
			BoInfo: accountLoginInBizParams,
			Bot:    bot,
		}
		//强制下线当前的bot，会挤掉原来在线的微信
		if WechatBotInfoMap[accountLoginInBizParams.WxPlatformNo].Bot != nil {
			if WechatBotInfoMap[accountLoginInBizParams.WxPlatformNo].Bot.Alive() {
				WechatBotInfoMap[accountLoginInBizParams.WxPlatformNo].Bot.Logout()
			}
		}

		if WechatBotInfoMap[accountLoginInBizParams.WxPlatformNo].BoInfo.CopilotNo == "" {
			redis.RPush("wxNoList", wxNo, 0)
		}

		// 更新map
		WechatBotInfoMap[accountLoginInBizParams.WxPlatformNo] = wechatBotInfo

		// 登录状态上报
		err = redis.Set(uuid+"-loginStatus:", loginStatus, time.Second*100)
		redis.Set("logining", "true", time.Second*100)
		if err != nil {
			log.Error(err)
		}
	}

	// 4 创建热存储容器对象
	// reloadStorage := openwechat.NewJsonFileHotReloadStorage("storage.json")

	// 5 执行热登录
	// err = bot.HotLogin(reloadStorage, true)
	// if err != nil {
	// 	if err = bot.Login(); err != nil {
	// 		log.Info("login error: %v \n", err)
	// 		return
	// 	}
	// }

	if loginStatus == "success" {
		// 阻塞主goroutine, 直到发生异常或者用户主动退出
		log.Info("login success: wxNo", wxNo, " wxNoWithWxPlatformNo:", accountLoginInBizParams.WxPlatformNo, " nickName: ", self.NickName)
		bot.Block()
	} else {
		bot.ExitWith(errors.New("登录微信号与绑定微信号不一致"))
	}
	log.Info("我能到这里面啊???，是因为退出了")
	return bot
}
