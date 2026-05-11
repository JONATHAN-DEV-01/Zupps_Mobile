package com.example.zuppsmobile.data.repository

import com.example.zuppsmobile.data.local.ProductDao
import com.example.zuppsmobile.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getProductsByRestaurant(restaurantId: Int): Flow<List<Product>> {
        return productDao.getProductsByRestaurant(restaurantId)
    }

    suspend fun insert(product: Product) = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)
}