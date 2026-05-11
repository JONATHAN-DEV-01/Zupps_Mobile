package com.example.zuppsmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zuppsmobile.data.repository.ProductRepository
import com.example.zuppsmobile.model.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _currentRestaurantId = MutableStateFlow<Int?>(null)

    fun setCurrentRestaurantId(id: Int?) {
        _currentRestaurantId.value = id
    }

    // O flatMapLatest faz com que a lista de produtos atualize quando o ID do restaurante mudar
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> = _currentRestaurantId.flatMapLatest { id ->
        if (id == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else repository.getProductsByRestaurant(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addProduct(name: String, description: String, price: Double, photoUri: String? = null) {
        val restaurantId = _currentRestaurantId.value ?: return
        viewModelScope.launch {
            val newProduct = Product(
                restaurantId = restaurantId,
                name = name,
                description = description,
                price = price,
                photoUri = photoUri
            )
            repository.insert(newProduct)
        }
    }

    suspend fun getProductById(id: Int): Product? {
        return repository.getProductById(id)
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.update(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
}

// Fábrica para instanciar o ViewModel com o Repositório
class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido")
    }
}