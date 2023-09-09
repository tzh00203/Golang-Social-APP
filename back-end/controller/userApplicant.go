package controller

import (
	"fmt"
	"net/http"
	"strings"

	"back-end/model"

	"github.com/gin-gonic/gin"
)

func MatchUsers(ctx *gin.Context) {
	// 解析前端传入的JSON数据
	var request struct {
		ReceiverUsername string `json:"receiverUsername"`
		SenderUsername   string `json:"senderUsername"`
	}

	if err := ctx.ShouldBindJSON(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{
			"error": "Invalid JSON input",
		})
		return
	}

	// 查询 receiverUsername 的信息
	var receiverUser model.User
	if err := model.DB.Where("name = ?", request.ReceiverUsername).First(&receiverUser).Error; err != nil {
		ctx.JSON(http.StatusNotFound, gin.H{
			"error": "Receiver user not found",
		})
		return
	}

	// 查询 senderUsername 的信息
	var senderUser model.User
	if err := model.DB.Where("name = ?", request.SenderUsername).First(&senderUser).Error; err != nil {
		ctx.JSON(http.StatusNotFound, gin.H{
			"error": "Sender user not found",
		})
		return
	}

	// 检查 senderUser 的 applicant 中是否包含 receiverUsername
	if !containsUsername(senderUser.Applicant, request.ReceiverUsername) {
		// 如果不包含，将 receiverUsername 添加到 senderUser 的 applicant 中
		senderUser.Applicant += "," + request.ReceiverUsername
		if err := model.DB.Save(&senderUser).Error; err != nil {
			ctx.JSON(http.StatusInternalServerError, gin.H{
				"error": "Failed to update sender user's applicant",
			})
			return
		}
	}

	// 检查 receiverUser 的 applicant 中是否包含 senderUsername
	if !containsUsername(receiverUser.Applicant, request.SenderUsername) {

		fmt.Println("Receiver User:", receiverUser)
		fmt.Println("Sender User:", senderUser)

		fmt.Println("Receiver User Applicant:", receiverUser.Applicant)
		fmt.Println("Sender User Applicant:", senderUser.Applicant)

		ctx.JSON(234, gin.H{
			"error": "Sender is not in the applicant list of receiver",
		})
		return
	}

	ctx.JSON(http.StatusOK, gin.H{
		"message": "Matched successfully",
	})
}

// 辅助函数，检查字符串是否包含指定的用户名
func containsUsername(applicant string, username string) bool {
	usernames := strings.Split(applicant, ",")
	for _, u := range usernames {
		if u == username {
			return true
		}
	}
	return false
}
