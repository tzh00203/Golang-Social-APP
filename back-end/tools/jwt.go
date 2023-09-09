package tools

import (
	"back-end/model"
	"time"

	"github.com/dgrijalva/jwt-go"
)

// jwt加密密钥
var jwtkey = []byte("your_jwt_key")

// 自定义token中的claim
type Claims struct {
	UserId uint
	jwt.StandardClaims
}

// 生成token
func ReleaseToken(user model.User) (string, error) {

	//设置Token的有效期
	expirationTime := time.Now().Add(7 * 24 * time.Hour)

	claims := &Claims{

		//自定义字段
		UserId: user.ID,
		//标准字段
		StandardClaims: jwt.StandardClaims{

			//过期时间
			ExpiresAt: expirationTime.Unix(),
			//发放时间
			IssuedAt: time.Now().Unix(),
			//发放者
			Issuer: "127.0.0.1",
			//主题
			Subject: "user token",
		},
	}

	//使用jwt密钥生成token
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString(jwtkey)

	if err != nil {
		return "", err
	}

	//返回token
	return tokenString, nil
}

// 从tokenString中解析出claims并返回
func ParseToken(tokenString string) (*jwt.Token, *Claims, error) {
	claims := &Claims{}

	token, err := jwt.ParseWithClaims(tokenString, claims, func(token *jwt.Token) (i interface{}, err error) {
		return jwtkey, nil
	})
	return token, claims, err
}
