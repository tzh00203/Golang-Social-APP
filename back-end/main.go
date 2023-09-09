package main

import (
	"back-end/config"
	"back-end/controller"
	"back-end/routes"
	"back-end/tools"
	"log"

	"github.com/gin-gonic/gin"
)

func main() {
	// 创建数据库
	config.CreateDB()
	// 创建数据库连接实例
	db := tools.InitDB()
	// 延迟关闭数据库连接
	defer db.Close()

	// 创建默认路由引擎
	r := gin.Default()

	// 启动路由
	routes.CollectRoutes(r)

	// 启动 WebSocket 服务器在 8080 端口上
	go controller.StartWebSocketServer()

	// 启动 HTTP 服务
	go func() {
		if err := r.Run(":8081"); err != nil {
			log.Fatal("Server startup error:", err)
		}
	}()

	// 阻塞主线程
	select {}
}
