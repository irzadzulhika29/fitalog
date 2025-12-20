package com.example.fitalog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitalog.data.AuthRepository
import com.example.fitalog.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val user = repository.getCurrentUser()
        _currentUser.value = user
        _isLoggedIn.value = user != null

        user?.let {
            viewModelScope.launch {
                repository.refreshUserData(it.uuid).onSuccess { refreshedUser ->
                    _currentUser.value = refreshedUser
                }
            }
        }
    }

    fun register(fullname: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Validation
            if (fullname.isBlank()) {
                _errorMessage.value = "Nama lengkap tidak boleh kosong"
                _isLoading.value = false
                return@launch
            }

            if (email.isBlank()) {
                _errorMessage.value = "Email tidak boleh kosong"
                _isLoading.value = false
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _errorMessage.value = "Format email tidak valid"
                _isLoading.value = false
                return@launch
            }

            if (password.length < 6) {
                _errorMessage.value = "Password minimal 6 karakter"
                _isLoading.value = false
                return@launch
            }

            repository.register(fullname, email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Registrasi gagal"
                    _isLoading.value = false
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Validation
            if (email.isBlank()) {
                _errorMessage.value = "Email tidak boleh kosong"
                _isLoading.value = false
                return@launch
            }

            if (password.isBlank()) {
                _errorMessage.value = "Password tidak boleh kosong"
                _isLoading.value = false
                return@launch
            }

            repository.login(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Login gagal"
                    _isLoading.value = false
                }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(application) as T
                }
            }
        }
    }
}

