package main

import (
	"fmt"
	"os"
	"runtime"
	"syscall"

	"bsinpass/go/middleware"
	"bsinpass/go/middleware/redis"
	"bsinpass/go/pkg/logging"
	log "bsinpass/go/pkg/logging"

	"bsinpass/go/router"

	config "bsinpass/go/conf"
	wechatApi "bsinpass/go/wechat/api"
	"bsinpass/go/wechat/wechatBot"

	"github.com/gin-gonic/gin"
	"github.com/robfig/cron/v3"
)

var cronTab *cron.Cron
var WxNoList []string

func init() {
	config.Setup()
	log.Setup()
	redis.Setup()
}

/*
* 从redis加载有用数据
 */
func redisImport() {
	// 从 wxNoList 中读取微信ID: wxNo,并加载wx配置数据
	WxNoList, err := redis.LRange("wxNoList")
	if err != nil {
		log.Warn(err)
	} else {
		log.Info(WxNoList)
	}
	for index, value := range WxNoList {
		log.Info("wxNo[", index, "]=", value)
		wxPlatformNo, err := redis.Get("wxPlatformNoWithWxNo:" + value)
		log.Info("wxPlatformNo:", wxPlatformNo)
		if err == nil {
			copilotNo, _ := redis.Get("copilotNoWithWxNo:" + value)
			requestIntervalLimit, _ := redis.Get("requestIntervalLimitWithWxNo:" + value)
			preResp, _ := redis.Get("preRespWithWxNo:" + value)
			groupChat, _ := redis.Get("groupChatWithWxNo:" + value)
			historyChatSummary, _ := redis.Get("historyChatSummaryWithWxNo:" + value)

			expirationTime, _ := redis.Get("expirationTimeWithWxNo:" + value)
			exceptionResp, _ := redis.Get("exceptionRespWithWxNo:" + value)

			nickName, _ := redis.Get("nickNameWithWxNo:" + value)

			// 更新WechatBotInfoMap
			boInfo := middleware.WechatLoginInOutBizParams{
				WxPlatformNo:         wxPlatformNo,
				CopilotNo:            copilotNo,
				RequestIntervalLimit: requestIntervalLimit,
				PreResp:              preResp,
				GroupChat:            groupChat,
				HistoryChatSummary:   historyChatSummary,
				ExpirationTime:       expirationTime,
				ExceptionResp:        exceptionResp,
				NickName:             nickName,
			}
			wechatBotInfo := wechatBot.WechatBotInfo{
				BoInfo: boInfo,
				Bot:    nil,
			}
			// 更新map
			wechatBot.WechatBotInfoMap[wxPlatformNo] = wechatBotInfo
		} else {
			log.Error(err)
		}

	}

}

var cronTabloop = 0

func cronTask() {
	// log.Info("定时任务：", time.Now())
	// 1.将wechat登录结果写入数据库
	cronTabloop++

	logining, err := redis.Get("logining")
	if err == nil && logining == "true" {
		uuid, err := redis.Get("uuid")
		if err == nil {
			nickname, err := redis.Get(uuid + "-nickname:")
			if err == nil {
				wxPlatformNo, err := redis.Get(uuid + "-wxPlatformNo:")
				if err == nil {
					wxNo, err := redis.Get(uuid + "-wxNo:")
					if err == nil {
						loginStatus, err := redis.Get(uuid + "-loginStatus:")
						if err == nil {
							loginResultRequestBizParams := middleware.LoginResultRequestBizParams{
								WxPlatformNo: wxPlatformNo,
								UUID:         uuid,
								Nickname:     nickname,
								WxNo:         wxNo,
								LoginStatus:  loginStatus,
							}
							//push登录结果
							log.Info("WechatLoginResulrtPush...")
							ret, err := wechatApi.WechatLoginResulrtPush(loginResultRequestBizParams)
							if err == nil {
								redis.Del(logining)
								redis.Del(uuid)
								redis.Del(uuid + "-nickname:")
								redis.Del(uuid + "-wxPlatformNo:")
								redis.Del(uuid + "-loginQrUrl:")
								redis.Del(uuid + "-wxNo:")
								redis.Del(uuid + "-loginStatus:")
							} else {
								log.Error(ret)
							}
						} else {
							log.Error(err)
						}
					} else {
						log.Error(err)
					}
				} else {
					log.Error(err)
				}
			} else {
				log.Error(err)
			}
		} else {
			log.Error(err)
		}
	}

	// 监控当前的在线wechatBot
	if cronTabloop%10 == 0 {
		log.Info("NumGoroutine: ", runtime.NumGoroutine())
		for wxPlatformNo := range wechatBot.WechatBotInfoMap {
			log.Info("当前登录的wx机器人ID: ", wxPlatformNo)
			var alive = false
			if wechatBot.WechatBotInfoMap[wxPlatformNo].Bot != nil {
				alive = wechatBot.WechatBotInfoMap[wxPlatformNo].Bot.Alive()
			}
			log.Info("登录状态:", alive)
			log.Info("RequestIntervalLimit:", wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.RequestIntervalLimit)
			log.Info("PreResp:", wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.PreResp)
			log.Info("GroupChat:", wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.GroupChat)
			log.Info("HistoryChatSummary:", wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.HistoryChatSummary)
			log.Info("ExpirationTime:", wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.ExpirationTime)
			log.Info("ExceptionResp:", wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.ExceptionResp)
			log.Info("NickNmae:", wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.NickName)
			log.Info("-------------------------------------------------")
		}
	}
}

func dealSignalExit(quit []chan int, proSignal chan os.Signal) {
	exit := false
	for !exit {
		select {
		case s := <-proSignal:
			if s == syscall.SIGINT || s == syscall.SIGTERM {
				for _, v := range quit {
					v <- 0
				}
				exit = true
			}
		}
	}
	logging.Error("Caught exit signal, process exited!")
	os.Exit(0)
}

// @description
// @title bsin-paas golang server
// @version 1.0
// @description bsin-paas golang 后台应用
// @contact.name leonard
// @contact.url https://www.s11edao.com
// @contact.email leijiwu001@gmail.com
// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html
// @host localhost:8126
func main() {

	gin.SetMode(config.ServerSetting.RunMode)
	routersInit := router.InitRouter()
	endPoint := fmt.Sprintf(":%d", config.ServerSetting.HttpPort)

	log.Info("[info] start http server listening %s", endPoint)
	log.Info("|-----------------------------------|")
	log.Info("|         bsin-golang server        |")
	log.Info("|-----------------------------------|")
	log.Info("|  Go Http Server Start Successful  |")
	log.Info("|     Port" + endPoint + "     Pid:" + fmt.Sprintf("%d", os.Getpid()) + "      |")
	log.Info("|-----------------------------------|")
	log.Info("")

	log.Info(config.AppSetting.AppName + "server running")

	redisImport()
	// 创建定时任务
	cronTab = cron.New(cron.WithSeconds())
	// 定时任务,cron表达式,每五秒一次
	spec := "*/5 * * * * ?"
	// 添加定时任务
	_, err := cronTab.AddFunc(spec, cronTask)
	if err != nil {
		log.Info("添加定时任务失败：", err)
	}
	// 启动定时器
	cronTab.Start()

	routersInit.Run(endPoint)

	// 阻塞主线程停止
	select {}
}
