package controller

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func Info(ctx *gin.Context) {

	user, _ := ctx.Get("user")
	//用户信息返回
	ctx.JSON(http.StatusOK, gin.H{
		"data": gin.H{"user": user},
	})
}
