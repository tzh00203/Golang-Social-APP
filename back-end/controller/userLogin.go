package controller

import (
	"back-end/model"
	"back-end/tools"
	"fmt"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
)

func Login(ctx *gin.Context) {
	db := tools.GetDB()

	// 处理前端数据
	var requestUser model.User
	ctx.BindJSON(&requestUser)
	name := requestUser.Name
	password := requestUser.Password

	//验证用户名和密码是否正常
	if len(name) == 0 || len(password) == 0 {
		ctx.JSON(http.StatusUnprocessableEntity, gin.H{
			"code":    422,
			"message": "用户名和密码不能为空",
		})
		return
	}

	//查询用户
	var user model.User
	if err := db.Where("name = ?", name).First(&user).Error; err != nil {
		ctx.JSON(http.StatusNotFound, gin.H{
			"code":    404,
			"message": "用户不存在",
		})
		return
	}

	//验证密码
	if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(password)); err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{
			"code":    401,
			"message": "密码错误",
		})
		return
	}

	// 更新用户的位置信息
	user.Location_Latitude = requestUser.Location_Latitude
	user.Location_Longitude = requestUser.Location_Longitude
	// 保存更新后的用户记录
	if err := db.Save(&user).Error; err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update user location"})
		fmt.Println("Error:", err)
		return
	}
	//发放token(更新1.0)
	token, err := tools.ReleaseToken(user)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{
			"code":    500,
			"message": "系统异常",
		})
		//记录错误
		log.Printf("token generate error: %v", err)
		return
	}

	//登录成功
	ctx.JSON(http.StatusOK, gin.H{
		"code":    200,
		"data":    gin.H{"token": token},
		"message": "登录成功",
	})
}
