package com.xxx.devcore.main.mine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ssz.network.LoginStateManager
import com.ssz.network.repository.UserCoinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ssz.network.Result

sealed class MineUiState {
    object Loading : MineUiState()

    data class Success(
        val coinCount: Int = 0,
        val username: String? = null,
        val nickname: String? = null,
        val userCoinData: com.ssz.network.model.UserCoinData? = null
    ) : MineUiState()

    data class Error(val message: String) : MineUiState()

    object NotLoggedIn : MineUiState()
}

class MineViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserCoinRepository()
    private val _uiState = MutableStateFlow<MineUiState>(MineUiState.Loading)
    val uiState: StateFlow<MineUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            // 先检查登录状态
            val isLoggedIn = LoginStateManager.isLoggedIn(getApplication())
            
            if (!isLoggedIn) {
                // 未登录，直接设置为未登录状态
                _uiState.value = MineUiState.NotLoggedIn
                return@launch
            }

            // 已登录，调用积分接口
            _uiState.value = MineUiState.Loading
            repository.getUserCoinInfo().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val data = result.data.data
                        if (data != null) {
                            _uiState.value = MineUiState.Success(
                                coinCount = data.coinCount,
                                username = data.username,
                                nickname = data.nickname,
                                userCoinData = data
                            )
                        } else {
                            _uiState.value = MineUiState.NotLoggedIn
                        }
                    }
                    is Result.Failure -> {
                        // 如果是未登录错误，显示未登录状态
                        if (result.exception.message?.contains("登录", ignoreCase = true) == true ||
                            result.exception.message?.contains("未登录", ignoreCase = true) == true) {
                            _uiState.value = MineUiState.NotLoggedIn
                        } else {
                            _uiState.value = MineUiState.Error(
                                result.exception.message ?: "获取用户信息失败"
                            )
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        loadUserInfo()
    }
}

