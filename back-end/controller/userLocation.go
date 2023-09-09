package controller

import (
	"log"
	"net/http"

	"back-end/model" // 导入模型包

	"github.com/gin-gonic/gin"
)

func GetUserLocationInfo(ctx *gin.Context) {
	var users []model.User
	var result []gin.H

	// 使用 GORM 查询获取所有用户的相关信息
	if err := model.DB.Select("name, location_latitude, location_longitude").Find(&users).Error; err != nil {
		// 处理错误，例如记录日志或返回错误响应
		ctx.JSON(http.StatusInternalServerError, gin.H{
			"error": "Failed to fetch user location info",
		})
		return
	}

	// 构建结果切片
	for _, user := range users {
		result = append(result, gin.H{
			"name":               user.Name,
			"location_latitude":  user.Location_Latitude,
			"location_longitude": user.Location_Longitude,
		})
	}

	// 返回用户信息的 JSON 响应
	ctx.JSON(http.StatusOK, gin.H{
		"data": result,
	})
	log.Println(result)

}
