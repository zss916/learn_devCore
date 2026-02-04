package com.xxx.devcore.main.navigation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssz.network.model.NavCategory
import com.ssz.network.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ssz.network.Result

sealed class NavigationUiState {
    object Loading : NavigationUiState()
    data class Success(val categories: List<NavCategory>) : NavigationUiState()
    data class Error(val message: String) : NavigationUiState()
}

class NavigationViewModel(
    private val repo: ArticleRepository = ArticleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NavigationUiState>(NavigationUiState.Loading)
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    init {
        // 只在首次创建时加载，如果已有数据则不重新加载
        if (_uiState.value is NavigationUiState.Loading) {
            load()
        }
    }

    fun load() {
        // 如果已经有数据，不重复加载
        if (_uiState.value is NavigationUiState.Success) {
            return
        }
        
        _uiState.value = NavigationUiState.Loading
        viewModelScope.launch {
            repo.getNavigation().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val categories = result.data.data ?: emptyList()
                        _uiState.value = NavigationUiState.Success(categories)
                    }
                    is Result.Failure -> {
                        _uiState.value = NavigationUiState.Error(
                            result.exception.message ?: "加载失败"
                        )
                    }
                }
            }
        }
    }
}

