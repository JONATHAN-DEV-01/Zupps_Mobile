package com.example.zuppsmobile.data.repository

import com.example.zuppsmobile.data.local.RestaurantDao
import com.example.zuppsmobile.model.Restaurant
import kotlinx.coroutines.flow.Flow

class RestaurantRepository(private val restaurantDao: RestaurantDao) {

    // O Flow fica "escutando" o banco. Se um restaurante for adicionado, a lista atualiza sozinha
    val allRestaurants: Flow<List<Restaurant>> = restaurantDao.getAllRestaurants()

    suspend fun insert(restaurant: Restaurant) {
        restaurantDao.insert(restaurant)
    }

    suspend fun getRestaurantByEmail(email: String): Restaurant? {
        return restaurantDao.getRestaurantByEmail(email)
    }

    suspend fun getRestaurantById(id: Int): Restaurant? {
        return restaurantDao.getRestaurantById(id)
    }

    suspend fun update(restaurant: Restaurant) {
        restaurantDao.update(restaurant)
    }

    suspend fun delete(restaurant: Restaurant) {
        restaurantDao.delete(restaurant)
    }
}