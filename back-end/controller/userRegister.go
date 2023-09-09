package controller

import (
	"back-end/model"
	"back-end/tools"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
)

func Register(ctx *gin.Context) {

	db := tools.GetDB()

	//处理前端数据
	var requestUser model.User
	ctx.Bind(&requestUser)
	name := requestUser.Name
	password := requestUser.Password
	log.Printf("Parsed User: %+v\n", requestUser)

	//数据验证
	if len(name) == 0 {
		ctx.JSON(http.StatusUnprocessableEntity, gin.H{
			"code":    422,
			"message": "用户名不能为空",
		})
		return
	}
	if len(password) < 6 {
		ctx.JSON(http.StatusUnprocessableEntity, gin.H{
			"code":    422,
			"message": "密码不能小于6位",
		})
		return
	}

	//判断用户名是否已经存在
	var existinguser model.User
	db.Where("name = ?", name).First(&existinguser)
	if existinguser.ID != 0 {
		ctx.JSON(http.StatusConflict, gin.H{
			"code":    409,
			"message": "用户名已存在",
		})
		return
	}

	//创建用户
	hasedpassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		ctx.JSON(http.StatusUnprocessableEntity, gin.H{
			"code":    500,
			"message": "密码加密错误",
		})
		return
	}
	newuser := model.User{
		Name:     name,
		Password: string(hasedpassword),
	}
	db.Create(&newuser)

	//注册成功，返回结果
	ctx.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "注册成功",
	})
}
