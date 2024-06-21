package copilot

import (
	config "bsinpass/go/conf"
	"bsinpass/go/middleware"
	log "bsinpass/go/pkg/logging"
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
)

// CopilotRequestBody data
type CopilotResponseData struct {
	Answer string `json:"answer"`
}

// CopilotRequestBody bizParams
type CopilotRequestBizParams struct {
	TenantId           string `json:"tenantId"`
	MerchantNo         string `json:"merchantNo"`
	CustomerNo         string `json:"customerNo"`
	CopilotNo          string `json:"copilotNo"`
	HistoryChatSummary string `json:"historyChatSummary"`
	Question           string `json:"question"`
}

// CopilotCompletions BsinAICopilot 文本模型回复
/*
{
    "serviceName": "ChatService",
    "methodName": "chatWithCopilot",
    "version": "config.LoadConfig().Version
    "bizParams": {
        "tenantId": "1737841274272223232",
        "merchantNo": "1737853502828482561",
        "customerNo": "1737853500064509954",
        "copilotNo": "1735682066449829888",
        "question": "用户的问题"
    }
}
*/

func CopilotCompletions(bizParams CopilotRequestBizParams) (string, error) {
	requestBody := middleware.BsinRequestBody{
		ServiceName: "CopilotService",
		MethodName:  "chat",
		Version:     config.WechatSetting.Version,
		BizParams:   bizParams,
	}

	requestData, err := json.Marshal(requestBody)

	if err != nil {
		log.Error("requestBody parse error : %v", err)
		return "", err
	}
	log.Debug("request copilot json string : %v", string(requestData))
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
	log.Debug(string(body))
	err = json.Unmarshal(body, copilotResponseBody)
	if err != nil {
		return "", err
	}
	var reply = ""
	if copilotResponseBody.Code == "000000" {
		jsonStr, err := json.Marshal(copilotResponseBody.Data)
		if err != nil {
			return "", err
		}
		var data CopilotResponseData
		err = json.Unmarshal(jsonStr, &data)
		reply = data.Answer
	} else {
		reply = copilotResponseBody.Message
	}
	log.Debug("copilot response text: %s \n", reply)
	return reply, nil
}
