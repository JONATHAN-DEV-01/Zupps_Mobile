package com.example.zuppsmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zuppsmobile.data.repository.RestaurantRepository
import com.example.zuppsmobile.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(private val repository: RestaurantRepository) : ViewModel() {

    // Lista de todos os restaurantes para a área admin
    val allRestaurants: StateFlow<List<Restaurant>> = repository.allRestaurants
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado para guardar mensagens de erro e mostrar na tela
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Estado para armazenar o restaurante logado
    private val _currentRestaurant = MutableStateFlow<Restaurant?>(null)
    val currentRestaurant: StateFlow<Restaurant?> = _currentRestaurant

    // Retorna true se o login der certo, false se falhar
    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Preencha todos os campos!"
            return
        }

        // viewModelScope abre uma corrotina (thread paralela) para não travar a tela
        viewModelScope.launch {
            try {
                val restaurant = repository.getRestaurantByEmail(email)
                if (restaurant != null && restaurant.passwordHash == pass) {
                    _errorMessage.value = null
                    _currentRestaurant.value = restaurant
                    onSuccess()
                } else {
                    _errorMessage.value = "E-mail ou senha incorretos."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao acessar o banco de dados: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun register(
        email: String, pass: String, tradeName: String, cnpj: String,
        phone: String, address: String, niche: String, hours: String,
        profilePhotoUri: String? = null, coverPhotoUri: String? = null,
        onSuccess: () -> Unit
    ) {
        // Validação básica de campos vazios
        if (email.isBlank() || pass.isBlank() || tradeName.isBlank() || cnpj.isBlank()) {
            _errorMessage.value = "Preencha os campos obrigatórios (E-mail, Senha, Nome e CNPJ)."
            return
        }

        if (pass.length < 6) {
            _errorMessage.value = "A senha deve ter pelo menos 6 caracteres."
            return
        }

        viewModelScope.launch {
            // Verifica se o email já existe
            val existing = repository.getRestaurantByEmail(email)
            if (existing != null) {
                _errorMessage.value = "Este e-mail já está cadastrado."
                return@launch
            }

            // Cria o objeto e salva no banco
            val newRestaurant = Restaurant(
                email = email,
                passwordHash = pass, // Em um app real, usaríamos hash (ex: BCrypt) aqui
                tradeName = tradeName,
                cnpj = cnpj,
                phone = phone,
                address = address,
                culinaryNiche = niche,
                operatingHours = hours,
                profilePhotoUri = profilePhotoUri,
                coverPhotoUri = coverPhotoUri
            )

            repository.insert(newRestaurant)
            _errorMessage.value = null
            // Após registrar, podemos logar automaticamente
            val registered = repository.getRestaurantByEmail(email)
            _currentRestaurant.value = registered
            onSuccess()
        }
    }

    fun logout() {
        _currentRestaurant.value = null
    }

    fun deleteRestaurant(restaurant: Restaurant) {
        viewModelScope.launch {
            repository.delete(restaurant)
        }
    }

    fun updateRestaurant(restaurant: Restaurant) {
        viewModelScope.launch {
            repository.update(restaurant)
        }
    }

    suspend fun getRestaurantById(id: Int): Restaurant? {
        return repository.getRestaurantById(id)
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

// Uma "Fábrica" é necessária para injetar o Repository dentro do ViewModel
class AuthViewModelFactory(private val repository: RestaurantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido")
    }
}