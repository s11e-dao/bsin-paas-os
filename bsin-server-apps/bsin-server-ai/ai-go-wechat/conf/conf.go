package conf

import (
	"log"
	"time"

	"github.com/go-ini/ini"
)

type App struct {
	JwtSecret string
	PageSize  int
	PrefixUrl string

	RuntimeRootPath string

	ImageSavePath  string
	ImageMaxSize   int
	ImageAllowExts []string

	ExportSavePath string
	QrCodeSavePath string
	FontSavePath   string

	AppName string

	LogSavePath string
	LogSaveName string
	LogFileExt  string
	TimeFormat  string
}

var AppSetting = &App{}

type Server struct {
	AppName      string
	RunMode      string
	HttpPort     int
	ReadTimeout  time.Duration
	WriteTimeout time.Duration
}

var ServerSetting = &Server{}

type Redis struct {
	Addr     string
	Password string
	DB       int
}

var RedisSetting = &Redis{}

type Wechat struct {
	BsinBaseUrl       string
	BsinCustomerToken string
	Version           string
	AutoPass          bool
	AutoPassResponse  string
	SessionTimeout    time.Duration
	SessionClearToken string
}

var WechatSetting = &Wechat{}

var cfg *ini.File

// Setup initialize the configuration instance
func Setup() {
	var err error
	cfg, err = ini.Load("conf/conf.ini")
	if err != nil {
		log.Fatalf("setting.Setup, fail to parse 'conf/conf.ini': %v", err)
	}

	mapTo("app", AppSetting)
	AppSetting.ImageMaxSize = AppSetting.ImageMaxSize * 1024 * 1024

	mapTo("server", ServerSetting)
	ServerSetting.ReadTimeout = ServerSetting.ReadTimeout * time.Second
	ServerSetting.WriteTimeout = ServerSetting.ReadTimeout * time.Second

	mapTo("redis", RedisSetting)

	mapTo("wechat", WechatSetting)
	WechatSetting.SessionTimeout = WechatSetting.SessionTimeout * time.Second

}

// mapTo map section
func mapTo(section string, v interface{}) {
	err := cfg.Section(section).MapTo(v)
	if err != nil {
		log.Fatalf("Cfg.MapTo RedisSetting err: %v", err)
	}
}
