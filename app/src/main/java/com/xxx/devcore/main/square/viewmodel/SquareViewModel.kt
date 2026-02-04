package com.xxx.devcore.main.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssz.network.model.Article
import com.ssz.network.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ssz.network.Result

sealed class SquareUiState {
    object Loading : SquareUiState()
    data class Success(val articles: List<Article>) : SquareUiState()
    data class Error(val message: String) : SquareUiState()
}

class SquareViewModel(
    private val repo: ArticleRepository = ArticleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<SquareUiState>(SquareUiState.Loading)
    val uiState: StateFlow<SquareUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load(page: Int = 0, pageSize: Int? = null) {
        _uiState.value = SquareUiState.Loading
        viewModelScope.launch {
            repo.getSquareArticles(page, pageSize).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val list = result.data.data?.datas ?: emptyList()
                        _uiState.value = SquareUiState.Success(list)
                    }
                    is Result.Failure -> {
                        _uiState.value = SquareUiState.Error(
                            result.exception.message ?: "加载失败"
                        )
                    }
                }
            }
        }
    }

    fun collectArticle(article: Article, isCollect: Boolean) {
        viewModelScope.launch {
            val result = if (isCollect) {
                repo.collect(article.id.toInt())
            } else {
                repo.unCollect(article.id.toInt())
            }
            
            when (result) {
                is Result.Success -> {
                    // 更新本地状态
                    val currentState = _uiState.value
                    if (currentState is SquareUiState.Success) {
                        val updatedArticles = currentState.articles.map { a ->
                            if (a.id == article.id) {
                                a.copy(collect = isCollect)
                            } else {
                                a
                            }
                        }
                        _uiState.value = SquareUiState.Success(updatedArticles)
                    }
                }
                is Result.Failure -> {
                    // 收藏/取消收藏失败，可以显示错误提示
                }
            }
        }
    }
}

