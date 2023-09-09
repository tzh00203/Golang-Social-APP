package middleware

import (
	"back-end/model"
	"back-end/tools"
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
)

func AuthMiddleware() gin.HandlerFunc {

	return func(ctx *gin.Context) {

		//获取authorization header
		tokenString := ctx.GetHeader("Authorization")

		//验证token格式
		if tokenString == "" || !strings.HasPrefix(tokenString, "Bearer") {
			ctx.JSON(http.StatusUnauthorized, gin.H{
				"code":    401,
				"message": "权限不足"})
			ctx.Abort()
			return
		}

		//提取token的有效部分
		tokenString = tokenString[7:]

		token, claims, err := tools.ParseToken(tokenString)
		if err != nil || !token.Valid {
			ctx.JSON(http.StatusUnauthorized, gin.H{
				"code":    401,
				"message": "权限不足"})
			ctx.Abort()
			return
		}

		// 验证通过后获取claim 中的userId
		userId := claims.UserId
		DB := tools.GetDB()
		var user model.User
		DB.First(&user, userId)

		//如果用户不存在
		if user.ID == 0 {
			ctx.JSON(http.StatusUnauthorized, gin.H{
				"code":    401,
				"message": "权限不足",
			})
			ctx.Abort()
			return
		}

		//用户存在则将user的信息写入上下文，方便读取
		ctx.Set("user", user)

		ctx.Next()
	}
}
