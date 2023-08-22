package main

import (
	"fmt"
	"net/http"
	"sync"

	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true //允许任何来源的WebSocket连接
	},
}

var clients = make(map[string]*websocket.Conn) //username和WebSocket连接的键值对
var clientsMutex sync.Mutex                    //连接互斥锁

func main() {
	r := gin.Default()

	r.LoadHTMLGlob("chat.html")

	r.GET("/:username", func(c *gin.Context) {
		username := c.Param("username") //从请求的url里解析username出来传到前端去
		c.HTML(http.StatusOK, "chat.html", gin.H{"username": username})
	})
	//前端来建立websocket连接
	r.GET("/ws", handleWebSocket)

	r.Run(":8080")
}

func handleWebSocket(c *gin.Context) {
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		fmt.Println("Error upgrading to WebSocket:", err)
		return
	}
	defer conn.Close()

	username := c.Query("username") //就用username做客户端标识

	clientsMutex.Lock() //在修改映射时，只有一个线程能够进行修改
	clients[username] = conn
	clientsMutex.Unlock()

	fmt.Println("Client connected:", username)

	for {
		messageType, p, err := conn.ReadMessage()
		if err != nil {
			fmt.Println("Error reading message:", err)
			break
		}

		//广播接收到的消息给所有客户端
		clientsMutex.Lock()
		for _, clientConn := range clients {
			err := clientConn.WriteMessage(messageType, p)
			if err != nil {
				fmt.Println("Error writing message:", err)
				clientConn.Close()
				delete(clients, username)
			}
		}
		clientsMutex.Unlock()
	}
}
