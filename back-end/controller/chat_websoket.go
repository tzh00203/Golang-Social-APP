package controller

import (
	"log"
	"net/http"
	"sync"

	"github.com/gorilla/websocket"
)

var (
	upgrader = websocket.Upgrader{
		ReadBufferSize:  1024,
		WriteBufferSize: 1024,
		CheckOrigin: func(r *http.Request) bool {
			return true
		},
	}
	//映射WebSocket连接到用户名的结构体
	clients = make(map[*websocket.Conn]string)

	mu sync.Mutex
)

type Message struct {
	SenderUsername   string `json:"senderUsername"`
	ReceiverUsername string `json:"receiverUsername"`
	Content          string `json:"content"`
}

func HandleWebSocket(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println(err)
		return
	}
	defer conn.Close()

	//从客户端消息中获取用户名
	var msg Message
	if err := conn.ReadJSON(&msg); err != nil {
		log.Println(err)
		return
	}

	mu.Lock() //同步锁
	//将连接和用户名关联
	clients[conn] = msg.SenderUsername
	mu.Unlock()

	//输出连接者的用户名
	log.Printf("Client connected: %s", msg.SenderUsername)

	for {
		var msg Message
		err := conn.ReadJSON(&msg)
		if err != nil {
			log.Println(err)
			mu.Lock()
			delete(clients, conn) //从客户端映射中删除断开连接的客户端
			mu.Unlock()
			return
		}

		log.Printf("Received message from %s to %s: %s", msg.SenderUsername, msg.ReceiverUsername, msg.Content)

		mu.Lock()
		//根据接收者的用户名查找接收者的连接
		for client, username := range clients {
			if username == msg.ReceiverUsername {
				if err := client.WriteJSON(msg); err != nil {
					log.Println(err)
				}
				break
			}
		}
		mu.Unlock()
	}
}

func StartWebSocketServer() {

	// 添加 WebSocket 路由
	http.HandleFunc("/ws", HandleWebSocket)
	// 启动 WebSocket 服务器在 8080 端口上
	log.Println("yunxing_before")
	err := http.ListenAndServe(":8080", nil)
	log.Println("yunxing")
	if err != nil {
		log.Fatal("WebSocket Server startup error:", err)
	}
	log.Println("yunxing_after")
}
