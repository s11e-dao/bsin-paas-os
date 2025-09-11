package conf

import (
	"fmt"
	"log"
	"os"
	"strconv"
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
	log.Printf("setting.Setup, success to parse 'conf/conf.ini'")

	mapTo("app", AppSetting)
	AppSetting.ImageMaxSize = AppSetting.ImageMaxSize * 1024 * 1024

	mapTo("server", ServerSetting)
	ServerSetting.ReadTimeout = ServerSetting.ReadTimeout * time.Second
	ServerSetting.WriteTimeout = ServerSetting.ReadTimeout * time.Second

	mapTo("redis", RedisSetting)

	mapTo("wechat", WechatSetting)
	WechatSetting.SessionTimeout = WechatSetting.SessionTimeout * time.Second

	// 从环境变量加载配置，如果环境变量存在则覆盖配置文件中的值
	loadFromEnv()

}

// mapTo map section
func mapTo(section string, v interface{}) {
	err := cfg.Section(section).MapTo(v)
	if err != nil {
		log.Fatalf("Cfg.MapTo RedisSetting err: %v", err)
	}
}

// loadFromEnv 从环境变量加载配置
func loadFromEnv() {
	// 加载 App 配置
	if prefixUrl := os.Getenv("GOLANG_SERVER_PREFIX_URL"); prefixUrl != "" {
		AppSetting.PrefixUrl = prefixUrl
		log.Printf("GOLANG_SERVER_PREFIX_URL: %s", prefixUrl)
		fmt.Println("GOLANG_SERVER_PREFIX_URL: ", prefixUrl)
	}

	// 加载 Server 配置
	if runMode := os.Getenv("GOLANG_SERVER_RUN_MODE"); runMode != "" {
		ServerSetting.RunMode = runMode
		log.Printf("GOLANG_SERVER_RUN_MODE: %s", runMode)
		fmt.Println("GOLANG_SERVER_RUN_MODE: ", runMode)
	}

	if httpPort := os.Getenv("GOLANG_SERVER_HTTP_PORT"); httpPort != "" {
		httpPortInt, err := strconv.Atoi(httpPort)
		if err != nil {
			log.Fatalf("GOLANG_SERVER_HTTP_PORT is not a valid integer: %v", err)
		}
		ServerSetting.HttpPort = httpPortInt
		log.Printf("GOLANG_SERVER_HTTP_PORT: %s", httpPort)
		fmt.Println("GOLANG_SERVER_HTTP_PORT: ", httpPort)
	}

	if readTimeout := os.Getenv("GOLANG_SERVER_READ_TIMEOUT"); readTimeout != "" {
		readTimeoutInt, err := strconv.Atoi(readTimeout)
		if err != nil {
			log.Fatalf("GOLANG_SERVER_READ_TIMEOUT is not a valid integer: %v", err)
		}
		ServerSetting.ReadTimeout = time.Duration(readTimeoutInt) * time.Second
		log.Printf("GOLANG_SERVER_READ_TIMEOUT: %s", readTimeout)
		fmt.Println("GOLANG_SERVER_READ_TIMEOUT: ", readTimeout)
	}

	if writeTimeout := os.Getenv("GOLANG_SERVER_WRITE_TIMEOUT"); writeTimeout != "" {
		writeTimeoutInt, err := strconv.Atoi(writeTimeout)
		if err != nil {
			log.Fatalf("GOLANG_SERVER_WRITE_TIMEOUT is not a valid integer: %v", err)
		}
		ServerSetting.WriteTimeout = time.Duration(writeTimeoutInt) * time.Second
		log.Printf("GOLANG_SERVER_WRITE_TIMEOUT: %s", writeTimeout)
		fmt.Println("GOLANG_SERVER_WRITE_TIMEOUT: ", writeTimeout)
	}

	// 加载 Redis 配置
	if redisAddr := os.Getenv("GOLANG_SERVER_REDIS_ADDR"); redisAddr != "" {
		RedisSetting.Addr = redisAddr
		log.Printf("GOLANG_SERVER_REDIS_ADDR: %s", redisAddr)
		fmt.Println("GOLANG_SERVER_REDIS_ADDR: ", redisAddr)
	}

	if redisPassword := os.Getenv("GOLANG_SERVER_REDIS_PASSWORD"); redisPassword != "" {
		RedisSetting.Password = redisPassword
		log.Printf("GOLANG_SERVER_REDIS_PASSWORD: %s", redisPassword)
		fmt.Println("GOLANG_SERVER_REDIS_PASSWORD: ", redisPassword)
	}

	if redisDb := os.Getenv("GOLANG_SERVER_REDIS_DB"); redisDb != "" {
		redisDbInt, err := strconv.Atoi(redisDb)
		if err != nil {
			log.Fatalf("GOLANG_SERVER_REDIS_DB is not a valid integer: %v", err)
		}
		RedisSetting.DB = redisDbInt
		log.Printf("GOLANG_SERVER_REDIS_DB: %s", redisDb)
		fmt.Println("GOLANG_SERVER_REDIS_DB: ", redisDb)
	}

	// 加载 Wechat 配置
	if bsinBaseUrl := os.Getenv("GOLANG_SERVER_BSIN_BASE_URL"); bsinBaseUrl != "" {
		WechatSetting.BsinBaseUrl = bsinBaseUrl
		log.Printf("GOLANG_SERVER_BSIN_BASE_URL: %s", bsinBaseUrl)
		fmt.Println("GOLANG_SERVER_BSIN_BASE_URL: ", bsinBaseUrl)
	}

	if bsinCustomerToken := os.Getenv("GOLANG_SERVER_BSIN_CUSTOMER_TOKEN"); bsinCustomerToken != "" {
		WechatSetting.BsinCustomerToken = bsinCustomerToken
		log.Printf("GOLANG_SERVER_BSIN_CUSTOMER_TOKEN: %s", bsinCustomerToken)
		fmt.Println("GOLANG_SERVER_BSIN_CUSTOMER_TOKEN: ", bsinCustomerToken)
	}

	if version := os.Getenv("GOLANG_SERVER_WECHAT_VERSION"); version != "" {
		WechatSetting.Version = version
		log.Printf("GOLANG_SERVER_WECHAT_VERSION: %s", version)
		fmt.Println("GOLANG_SERVER_WECHAT_VERSION: ", version)
	}

	if autoPass := os.Getenv("GOLANG_SERVER_WECHAT_AUTO_PASS"); autoPass != "" {
		WechatSetting.AutoPass = autoPass == "true"
		log.Printf("GOLANG_SERVER_WECHAT_AUTO_PASS: %s", autoPass)
		fmt.Println("GOLANG_SERVER_WECHAT_AUTO_PASS: ", autoPass)
	}

	if autoPassResponse := os.Getenv("GOLANG_SERVER_WECHAT_AUTO_PASS_RESPONSE"); autoPassResponse != "" {
		WechatSetting.AutoPassResponse = autoPassResponse
		log.Printf("GOLANG_SERVER_WECHAT_AUTO_PASS_RESPONSE: %s", autoPassResponse)
		fmt.Println("GOLANG_SERVER_WECHAT_AUTO_PASS_RESPONSE: ", autoPassResponse)
	}

	if sessionTimeout := os.Getenv("GOLANG_SERVER_WECHAT_SESSION_TIMEOUT"); sessionTimeout != "" {
		sessionTimeoutInt, err := strconv.Atoi(sessionTimeout)
		if err != nil {
			log.Fatalf("GOLANG_SERVER_WECHAT_SESSION_TIMEOUT is not a valid integer: %v", err)
		}
		WechatSetting.SessionTimeout = time.Duration(sessionTimeoutInt) * time.Second
		log.Printf("GOLANG_SERVER_WECHAT_SESSION_TIMEOUT: %s", sessionTimeout)
		fmt.Println("GOLANG_SERVER_WECHAT_SESSION_TIMEOUT: ", sessionTimeout)
	}

	if sessionClearToken := os.Getenv("GOLANG_SERVER_WECHAT_SESSION_CLEAR_TOKEN"); sessionClearToken != "" {
		WechatSetting.SessionClearToken = sessionClearToken
		log.Printf("GOLANG_SERVER_WECHAT_SESSION_CLEAR_TOKEN: %s", sessionClearToken)
		fmt.Println("GOLANG_SERVER_WECHAT_SESSION_CLEAR_TOKEN: ", sessionClearToken)
	}
}
