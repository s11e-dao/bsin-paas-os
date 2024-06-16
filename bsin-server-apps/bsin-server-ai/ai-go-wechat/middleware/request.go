package middleware

type BsinRequestBody struct {
	// Required: true
	ServiceName string      `json:"serviceName"`
	MethodName  string      `json:"methodName"`
	Version     string      `json:"version"`
	BizParams   interface{} `json:"bizParams"`
}

type BsinResponse struct {
	// Required: true
	Code    string      `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

type WechatLoginInOutBizParams struct {
	WxPlatformNo         string `json:"wxPlatformNo"`
	CopilotNo            string `json:"copilotNo"`
	RequestIntervalLimit string `json:"requestIntervalLimit"`
	PreResp              string `json:"preResp"`
	GroupChat            string `json:"groupChat"`
	HistoryChatSummary   string `json:"historyChatSummary"`
	ExpirationTime       string `json:"expirationTime"`
	ExceptionResp        string `json:"exceptionResp"`
	Operation            string `json:"operation"`
	NickName             string `json:"nickName"`
	Password             string `json:"password"`
}

type WechatLoginOutput struct {
	LoginQrUrl string `json:"loginQrUrl"`
	Code       int    `json:"code"`
	Message    string `json:"message"`
}

// wechatLoginResult bizParams
type LoginResultRequestBizParams struct {
	WxPlatformNo string `json:"wxPlatformNo"`
	UUID         string `json:"uuid"`
	Nickname     string `json:"nickname"`
	WxNo         string `json:"wxNo"`
	LoginStatus  string `json:"loginStatus"`
}
