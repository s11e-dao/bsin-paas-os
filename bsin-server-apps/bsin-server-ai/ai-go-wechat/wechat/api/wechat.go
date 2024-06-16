package api

import (
	"bsinpass/go/common"
	config "bsinpass/go/conf"
	"bsinpass/go/middleware"
	log "bsinpass/go/pkg/logging"
	"bsinpass/go/wechat/wechatBot"
	"bytes"
	_ "crypto/sha256"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"time"

	"bsinpass/go/middleware/redis"

	"github.com/gin-gonic/gin"
)

func WechatLoginInRet(w http.ResponseWriter, out *middleware.WechatLoginOutput) {
	v, err := json.Marshal(out)
	if err != nil {
		log.Error(err)
		return
	}
	w.Write(v)
}

func WechatBotMonitorRet(w http.ResponseWriter, out *WechatBotMonitorOutput) {
	v, err := json.Marshal(out)
	if err != nil {
		log.Error(err)
		return
	}
	w.Write(v)
}

func setWechatLoginOutput(out *middleware.WechatLoginOutput, loginQrUrl string, err common.BsinErrorCode) {
	out.LoginQrUrl = loginQrUrl
	out.Code = err.ErrorNo
	out.Message = err.ErrorMsg
}

type WechatBotMonitorOutput struct {
	WechatBotMonitorInfos []wechatBot.WechatBotMonitorInfo `json:"wechatBotMonitorInfo"`
	Code                  int                              `json:"code"`
	Message               string                           `json:"message"`
}

func setWechatBotMonitorOutput(out *WechatBotMonitorOutput, wechatBotMonitorInfo []wechatBot.WechatBotMonitorInfo, err common.BsinErrorCode) {
	out.WechatBotMonitorInfos = wechatBotMonitorInfo
	out.Code = err.ErrorNo
	out.Message = err.ErrorMsg
}

// @Summary 微信登录
// @title wechat API
// @version 0.0.1
// @description   wechat login
// @Tags wechat/login
// @Id 11
// @BasePath /v1/wechat/login
// @Host 127.0.0.1:8126
// @Produce  json
// @Param sign body string true "签名信息:暂时无用，可以随便写死一个"
// @Param wxPlatformNo body string true "bsin-copilot 微信平台ID"
// @Param copilotNo body string true "bsin-copilot ID"
// @Param loginIn body string true "true|false: login or out"
// @Success 200 {string} json "{"code":0,"LoginQrUrl":"登录二维码","message":"ok"}"
// @Failure 400 {string} json "{"code":600,"message":"failed"}"
// @Router /account/createUser [post]
func LogInOutIf(c *gin.Context) {
	input := middleware.BsinRequestBody{}
	inputBizParams := middleware.WechatLoginInOutBizParams{}
	output := middleware.WechatLoginOutput{}
	defer WechatLoginInRet(c.Writer, &output)
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		log.Error("Read LogInOutIf http request body failed! [%v]", err)
		setWechatLoginOutput(&output, "", common.NewError(400))
		return
	}
	err = json.Unmarshal(body, &input)
	log.Info("LogInOutIf body:", string(body))
	if err != nil {
		log.Error("Decode LogInOutIf http request body failed! [%v]", err)
		setWechatLoginOutput(&output, "", common.NewError(400))
		return
	}

	jsonStr, err := json.Marshal(input.BizParams)
	if err != nil {
		log.Error("Encode LogInOutIf http request body  BizParams failed! [%v]", err)
		return
	}
	err = json.Unmarshal(jsonStr, &inputBizParams)
	log.Info("LogInOutIf body BizParams:", string(jsonStr))
	if err != nil {
		log.Error("Decode LogInOutIf http request body BizParams failed! [%v]", err)
		setWechatLoginOutput(&output, "", common.NewError(400))
		return
	}
	log.Info("inputBizParams.WxPlatformNo:", inputBizParams.WxPlatformNo)
	log.Info("inputBizParams.PreResp:", inputBizParams.PreResp)
	log.Info("inputBizParams.GroupChat:", inputBizParams.GroupChat)
	log.Info("inputBizParams.HistoryChatSummary:", inputBizParams.HistoryChatSummary)
	log.Info("inputBizParams.RequestIntervalLimit:", inputBizParams.RequestIntervalLimit)
	log.Info("inputBizParams.CopilotNo:", inputBizParams.CopilotNo)
	log.Info("inputBizParams.ExpirationTime:", inputBizParams.ExpirationTime)
	log.Info("inputBizParams.ExceptionResp:", inputBizParams.ExceptionResp)

	// 登录
	if inputBizParams.Operation == "loginWechat" {
		// 创建一个 goroutine
		go wechatBot.Run(inputBizParams)
		var cycle = 0
		for {
			uuid, err := redis.Get("uuid")
			log.Info("uuid:", uuid, err)
			if err != nil {
				log.Error(err)
			} else {
				uuidQRurl, err := redis.Get(fmt.Sprint(uuid) + "-loginQrUrl:")
				if err != nil {
					log.Error("不能找到： ", uuid, " 对应的loginQrUrl")
					log.Error(err)
				} else {
					log.Info("loginQrUrl:", uuidQRurl, err)
					setWechatLoginOutput(&output, fmt.Sprint(uuidQRurl), common.NewError(0))
					break
				}
			}
			time.Sleep(time.Second * 2)
			cycle++
			if cycle > 10 {
				setWechatLoginOutput(&output, "", common.NewError(600))
				break
			}
		}
	} else
	// 退出
	{
		if wechatBot.WechatBotInfoMap[inputBizParams.WxPlatformNo].Bot != nil {
			if wechatBot.WechatBotInfoMap[inputBizParams.WxPlatformNo].Bot.Alive() {
				wechatBot.WechatBotInfoMap[inputBizParams.WxPlatformNo].Bot.Logout()
			} else {
				log.Warn("WechatBot is already deaded")
			}
			// TODO: 是否删除记录
			// delete 是一个内置函数，如果 key 存在，就删除该 key-value,如果 key 不存在， 不操作，但是也不会报错
			// delete(wechatBot.WechatBotInfoMap, "inputBizParams.WxPlatformNo")
		} else {
			log.Warn("WechatBot not exist")
		}
		setWechatLoginOutput(&output, "", common.NewError(0))
	}
	return
}

// @Summary 微信机器人监控接口： 总共登录的微信|在线的微信|。。。
// @title wechat API
// @version 0.0.1
// @description   monitor
// @Tags wechat/monitor
// @Id 11
// @BasePath /v1/wechat/monitor
// @Host 127.0.0.1:8126
// @Produce  json
// @Param sign body string true "签名信息:暂时无用，可以随便写死一个"
// @Success 200 {string} json "{"code":0,"wechatBotInfoList":WechatBotInfoMap,"message":"ok"}"
// @Failure 400 {string} json "{"code":600,"message":"failed"}"
// @Router /account/createUser [post]
func WechatBotMonitorIf(c *gin.Context) {
	input := middleware.BsinRequestBody{}
	output := WechatBotMonitorOutput{}
	defer WechatBotMonitorRet(c.Writer, &output)
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		log.Error("Read WechatBotMonitorIf http request body failed! [%v]", err)
		setWechatBotMonitorOutput(&output, nil, common.NewError(400))
		return
	}
	err = json.Unmarshal(body, &input)
	log.Info("WechatBotMonitorIf body:", string(body))
	if err != nil {
		log.Error("Decode WechatBotMonitorIf http request body failed! [%v]", err)
		setWechatBotMonitorOutput(&output, nil, common.NewError(400))
		return
	}
	wechatBotInfos := []wechatBot.WechatBotMonitorInfo{}
	var alive = false
	for wxPlatformNo := range wechatBot.WechatBotInfoMap {
		if wechatBot.WechatBotInfoMap[wxPlatformNo].Bot != nil {
			alive = wechatBot.WechatBotInfoMap[wxPlatformNo].Bot.Alive()
		} else {
			alive = false
		}
		wechatBotInfo := wechatBot.WechatBotMonitorInfo{
			WxPlatformNo:         wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.WxPlatformNo,
			CopilotNo:            wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.CopilotNo,
			RequestIntervalLimit: wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.RequestIntervalLimit,
			PreResp:              wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.PreResp,
			GroupChat:            wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.GroupChat,
			HistoryChatSummary:   wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.HistoryChatSummary,
			ExpirationTime:       wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.ExpirationTime,
			ExceptionResp:        wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.ExceptionResp,
			LoginIn:              wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.Operation,
			NickName:             wechatBot.WechatBotInfoMap[wxPlatformNo].BoInfo.NickName,
			Alive:                alive,
		}
		wechatBotInfos = append(wechatBotInfos, wechatBotInfo)
	}
	for wechatBotInfo := range wechatBotInfos {
		log.Info("wechatBotInfo:", string(wechatBotInfo))
	}
	setWechatBotMonitorOutput(&output, wechatBotInfos, common.NewError(0))

	return
}

func WechatLoginResulrtPush(bizParams middleware.LoginResultRequestBizParams) (string, error) {
	requestBody := middleware.BsinRequestBody{
		ServiceName: "WxPlatformService",
		MethodName:  "updateLoginResult",
		Version:     config.WechatSetting.Version,
		BizParams:   bizParams,
	}
	requestData, err := json.Marshal(requestBody)

	if err != nil {
		log.Info("requestBody parse error : %v", err)
		return "", err
	}
	log.Info("request copilot json string : %v", string(requestData))
	req, err := http.NewRequest("POST", config.WechatSetting.BsinBaseUrl, bytes.NewBuffer(requestData))
	if err != nil {
		return "", err
	}

	bsinCustomerToken := config.WechatSetting.BsinCustomerToken
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", bsinCustomerToken)
	client := &http.Client{}
	response, err := client.Do(req)
	if err != nil {
		return "", err
	}
	defer response.Body.Close()
	if response.StatusCode != 200 {
		return "", errors.New(fmt.Sprintf("copilot service status code not equals 200,code is %d", response.StatusCode))
	}
	body, err := ioutil.ReadAll(response.Body)
	if err != nil {
		return "", err
	}

	copilotResponseBody := &middleware.BsinResponse{}
	log.Info(string(body))
	err = json.Unmarshal(body, copilotResponseBody)
	if err != nil {
		return "", err
	}
	if copilotResponseBody.Code == "000000" {
		log.Info("WechatLoginResulrtPush: ", copilotResponseBody.Message)
	} else {
		log.Error("WechatLoginResulrtPush: ", copilotResponseBody.Message)
	}
	return string(body), nil
}
