package com.example.zuppsmobile.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val tradeName: String,
    val cnpj: String,
    val phone: String,
    val address: String,
    val culinaryNiche: String,
    val operatingHours: String,
    val profilePhotoUri: String? = null,
    val coverPhotoUri: String? = null
)