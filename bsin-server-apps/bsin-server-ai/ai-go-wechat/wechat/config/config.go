package config

import (
	log "bsinpass/go/pkg/logging"
	"encoding/json"
	"os"
	"sync"
	"time"
)

// WechatConfiguration 项目配置
type WechatConfiguration struct {
	//Bsin gateway
	BsinBaseUrl       string `json:"bsin_base_url"`
	BsinCustomerToken string `json:"bsin_customer_token"`
	Version           string `json:"version"`
	// 自动通过好友
	AutoPass bool `json:"auto_pass"`
	// 自动通过好友的回复
	AutoPassResponse string `json:"auto_pass_response"`
	// 会话超时时间
	SessionTimeout time.Duration `json:"session_timeout"`
	// 清空会话口令
	SessionClearToken string `json:"session_clear_token"`
}

var config *WechatConfiguration
var once sync.Once

// LoadConfig 加载配置
func LoadWechatConfig() *WechatConfiguration {
	once.Do(func() {
		// 给配置赋默认值
		config = &WechatConfiguration{
			AutoPass: false,
		}

		// 判断配置文件是否存在，存在直接JSON读取
		f, err := os.Open("config.json")
		if err != nil {
			log.Error("open config err: %v", err)
			return
		}
		defer f.Close()
		encoder := json.NewDecoder(f)
		err = encoder.Decode(config)
		if err != nil {
			log.Error("decode config err: %v", err)
			return
		}

		// 如果环境变量有配置，读取环境变量
		AutoPass := os.Getenv("AUTO_PASS")
		SessionTimeout := os.Getenv("SESSION_TIMEOUT")
		SessionClearToken := os.Getenv("SESSION_CLEAR_TOKEN")

		if AutoPass == "true" {
			config.AutoPass = true
		}

		if SessionTimeout != "" {
			duration, err := time.ParseDuration(SessionTimeout)
			if err != nil {
				log.Error("config decode session timeout err: %v ,get is %v", err, SessionTimeout)
				return
			}
			config.SessionTimeout = duration
		}

		if SessionClearToken != "" {
			config.SessionClearToken = SessionClearToken
		}
	})

	return config
}
