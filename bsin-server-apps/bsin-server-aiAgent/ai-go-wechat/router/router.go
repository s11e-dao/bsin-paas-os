package router

import (
	_ "bsinpass/go/docs"

	// corsCtr "bsinpass/go/middleware/cors"

	"github.com/gin-gonic/gin"

	wechatApi "bsinpass/go/wechat/api"
)

// InitRouter initialize routing information
func InitRouter() *gin.Engine {

	// r := gin.New()
	// r.Use(gin.Logger())
	// r.Use(gin.Recovery())
	//允许使用跨域请求  全局中间件
	// r.Use(corsCtr.Cors())

	r := gin.Default() // 函数返回的是一个Engine指针，Engine代表的是整个框架的一个实例，查看源码可发现实际就是调用New()方法创建实例,并且为实例添加了Logger和Recovery中间件.

	// 添加swagger的路由  不然报错404 page not found
	// r.GET("/swagger/*any", ginSwagger.DisablingWrapHandler(swaggerFiles.Handler, "NAME_OF_ENV_VARIABLE"))

	//! 1、wechat
	bisngo := r.Group("/bsingo")
	{
		//! 1.1、 account
		wechat := bisngo.Group("/wechat")
		{
			// 登录|退出
			wechat.POST("/login", wechatApi.LogInOutIf)
			// 监控
			wechat.POST("/monitor", wechatApi.WechatBotMonitorIf)

		}
	}

	return r
}
