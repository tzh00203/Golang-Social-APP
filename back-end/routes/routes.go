package routes

import (
	"back-end/controller"

	"github.com/gin-gonic/gin"
)

func CollectRoutes(r *gin.Engine) *gin.Engine {

	//注册
	r.POST("/register", controller.Register)
	//登录
	r.POST("/login", controller.Login)
	//返回用户信息
	r.POST("/locationinfo", controller.GetUserLocationInfo)
	//双向匹配
	r.POST("/match", controller.MatchUsers)
	return r
}
