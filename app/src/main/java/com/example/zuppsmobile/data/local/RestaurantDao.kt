package com.example.zuppsmobile.data.local

import androidx.room.*
import com.example.zuppsmobile.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(restaurant: Restaurant)

    @Query("SELECT * FROM restaurants WHERE email = :email LIMIT 1")
    suspend fun getRestaurantByEmail(email: String): Restaurant?

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): Flow<List<Restaurant>>

    @Update
    suspend fun update(restaurant: Restaurant)

    @Delete
    suspend fun delete(restaurant: Restaurant)

    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun getRestaurantById(id: Int): Restaurant?
}