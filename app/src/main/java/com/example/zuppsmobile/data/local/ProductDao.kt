package com.example.zuppsmobile.data.local

import androidx.room.*
import com.example.zuppsmobile.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: Product)

    @Query("SELECT * FROM products WHERE restaurantId = :restaurantId")
    fun getProductsByRestaurant(restaurantId: Int): Flow<List<Product>>

    // Adicione esta linha:
    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?
}