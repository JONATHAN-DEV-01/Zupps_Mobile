package com.example.zuppsmobile

import android.app.Application
import com.example.zuppsmobile.data.local.AppDatabase
import com.example.zuppsmobile.data.repository.ProductRepository
import com.example.zuppsmobile.data.repository.RestaurantRepository

class ZuppsApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { RestaurantRepository(database.restaurantDao()) }

    // Adicione esta linha:
    val productRepository by lazy { ProductRepository(database.productDao()) }
}