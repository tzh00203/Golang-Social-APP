package model

import "github.com/jinzhu/gorm"

type User struct {
	gorm.Model
	Name               string  `gorm:"varchar(20);not null" json:"name"`
	Password           string  `gorm:"size:255;not null" json:"password"`
	Location_Latitude  float64 `gorm:"type:double" json:"location_latitude"`
	Location_Longitude float64 `gorm:"type:double" json:"location_longitude"`
	Applicant          string  `gorm:"type:text" json:"applicant"`
}
