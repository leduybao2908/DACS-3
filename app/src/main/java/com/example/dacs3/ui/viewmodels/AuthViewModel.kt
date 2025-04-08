package com.example.dacs3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.User
import com.example.dacs3.data.UserDao
import com.example.dacs3.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthEvent {
    object NavigateToLogin : AuthEvent()
    data class ShowError(val message: String) : AuthEvent()
}

class AuthViewModel(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _authEvent = MutableStateFlow<AuthEvent?>(null)
    val authEvent: StateFlow<AuthEvent?> = _authEvent

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    init {
        viewModelScope.launch {
            // Kiểm tra user đã lưu trước đó
            userPreferences.getUserFlow().collect { user ->
                user?.let {
                    _authState.value = AuthState.Success(it)
                    _accessToken.value = it.token // Lấy token từ user đã đăng nhập trước đó
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authEvent.value = null
                val user = userDao.login(email, password)
                if (user != null) {
                    handleLoginSuccess(user)
                } else {
                    _authEvent.value = AuthEvent.ShowError("Invalid credentials")
                }
            } catch (e: Exception) {
                _authEvent.value = AuthEvent.ShowError(e.message ?: "Unknown error")
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    _authEvent.value = AuthEvent.ShowError("Email already exists")
                    return@launch
                }

                // Giả sử server sẽ cung cấp token sau khi đăng ký
                val accessToken = fetchAccessTokenFromServer(email, password)

                val user = User(
                    username = username,
                    email = email,
                    password = password,
                    token = accessToken
                )

                userDao.insertUser(user)
                userPreferences.saveUser(user)
                _authState.value = AuthState.Success(user)
                _accessToken.value = accessToken // Cập nhật token
            } catch (e: Exception) {
                _authEvent.value = AuthEvent.ShowError(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userPreferences.clearUser()
                _authState.value = AuthState.Initial
                _accessToken.value = null
                _authEvent.value = AuthEvent.NavigateToLogin
            } catch (e: Exception) {
                _authEvent.value = AuthEvent.ShowError("Failed to logout: ${e.message}")
            }
        }
    }

    fun clearEvents() {
        _authEvent.value = null
    }

    private suspend fun handleLoginSuccess(user: User) {
        try {
            userPreferences.saveUser(user)
            _authState.value = AuthState.Success(user)
            _accessToken.value = user.token // Cập nhật token khi đăng nhập
        } catch (e: Exception) {
            _authEvent.value = AuthEvent.ShowError("Failed to save user session: ${e.message}")
        }
    }

    private suspend fun fetchAccessTokenFromServer(email: String, password: String): String {
        // Giả lập lấy token từ server
        return "fake_access_token_${email.hashCode()}"
    }
}

sealed class AuthState {
    object Initial : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
