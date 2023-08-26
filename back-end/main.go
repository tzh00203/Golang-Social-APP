package main

import (
	"back-end/config"
	"back-end/routes"
	"back-end/tools"
	"log"

	"github.com/gin-gonic/gin"
)

func main() {

	//创建数据库
	config.CreateDB()
	//创建数据库连接实例
	db := tools.InitDB()
	//延迟关闭数据库连接
	defer db.Close()

	//创建默认路由引擎
	r := gin.Default()

	//启动路由
	routes.CollectRoutes(r)

	//在8080端口启动服务
	err := r.Run(":8080")
	if err != nil {
		log.Fatal("Sever startup error:", err)
	}
}
