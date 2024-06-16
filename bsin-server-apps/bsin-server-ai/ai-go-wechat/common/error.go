package common

type BsinErrorCode struct {
	ErrorNo  int    // 错误码
	ErrorMsg string // 错误描述
}

var errMsg map[int]string

func init() {
	errMsg = make(map[int]string)
	errMsg[000000] = "ok"
	errMsg[400] = "request input error"
	errMsg[401] = "auth failed"
	errMsg[402] = "get url file failed"
	errMsg[403] = "input tokenid not correct"
	errMsg[404] = "input amount not correct"
	errMsg[405] = "input token supply not correct"
	errMsg[406] = "input payableAmount not correct"
	errMsg[407] = "input upperBound not correct"
	errMsg[408] = "input payableAmount and upperBound not match"
	errMsg[409] = "get local file failed"
	errMsg[500] = "visit chain node failed"
	errMsg[501] = "can not visit input caller address"
	errMsg[502] = "trade to blockchain failed"
	errMsg[503] = "not set ipfs node path"
	errMsg[504] = "upload file to ipfs failed"
	errMsg[505] = "unsupported chain"
	errMsg[506] = "failed to visit input contract"
	errMsg[507] = "can not create new account"
	errMsg[508] = "can not create cfx client"
	errMsg[509] = "can not get account"
	errMsg[510] = "deploy failed"
	errMsg[511] = "get sign failed"
	errMsg[512] = "new contract failed"
	errMsg[513] = "not support yet"
	errMsg[514] = "confirm to blockchain failed"
	errMsg[600] = "wechat login timeout"
	errMsg[1000] = "redis null key"
}

func NewError(code int) BsinErrorCode {
	v, ok := errMsg[code]
	if ok {
		return BsinErrorCode{ErrorNo: code, ErrorMsg: v}
	} else {
		return BsinErrorCode{ErrorNo: code, ErrorMsg: "Unknown error"}
	}
}

func NewErrorMsg(msg string) BsinErrorCode {
	return BsinErrorCode{ErrorNo: 100000, ErrorMsg: msg}

}

func Valid(err BsinErrorCode) bool {
	return err.ErrorNo == 000000
}
