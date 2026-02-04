package com.ssz.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssz.network.repository.LoginRepository
import com.ssz.network.repository.RegisterRepository
import com.ssz.network.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/// todo 数据模型
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val rePassword: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel : ViewModel() {

    ///todo uiState
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    ///todo api
    private val loginRepository = LoginRepository()
    private val registerRepository = RegisterRepository()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }
    
    fun onRePasswordChange(value: String) {
        _uiState.update { it.copy(rePassword = value, errorMessage = null) }
    }
    
    fun toggleMode() {
        _uiState.update { 
            it.copy(
                isRegisterMode = !it.isRegisterMode,
                password = "",
                rePassword = "",
                errorMessage = null
            ) 
        }
    }

    fun login() {
        val current = _uiState.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "请输入用户名和密码") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            loginRepository.login(current.username, current.password)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                        }
                        is Result.Failure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.exception.message ?: "登录失败"
                                )
                            }
                        }
                    }
                }
        }
    }
    
    fun register() {
        val current = _uiState.value
        if (current.username.isBlank() || current.password.isBlank() || current.rePassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "请填写完整信息") }
            return
        }
        
        if (current.password != current.rePassword) {
            _uiState.update { it.copy(errorMessage = "两次输入的密码不一致") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            registerRepository.register(current.username, current.password, current.rePassword)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "注册成功，请登录",
                                    isRegisterMode = false,
                                    password = "",
                                    rePassword = ""
                                ) 
                            }
                        }
                        is Result.Failure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.exception.message ?: "注册失败"
                                )
                            }
                        }
                    }
                }
        }
    }

    fun clearData() {
        _uiState.update { LoginUiState() }
    }
}

