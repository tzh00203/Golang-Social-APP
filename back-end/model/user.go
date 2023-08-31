package model

import "github.com/jinzhu/gorm"

type User struct {
	gorm.Model
	Name              string  `gorm:"varchar(20);not null" json:"name"`
	Password          string  `gorm:"size:255;not null" json:"password"`
	LocationLatitude  float64 `gorm:"type:double" json:"location_latitude"`
	LocationLongitude float64 `gorm:"type:double" json:"location_longitude"`
}
