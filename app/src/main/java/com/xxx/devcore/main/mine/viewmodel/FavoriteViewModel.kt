package com.xxx.devcore.main.mine.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssz.network.model.Article
import com.ssz.network.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.ssz.network.Result

sealed class FavoriteUiState {
    object Loading : FavoriteUiState()
    data class Success(
        val articles: List<Article>,
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false,
        val hasMore: Boolean = true
    ) : FavoriteUiState()
    data class Error(val message: String) : FavoriteUiState()
}

class FavoriteViewModel(
    private val repo: ArticleRepository = ArticleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoriteUiState>(FavoriteUiState.Loading)
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var totalPages = 1

    init {
        load()
    }

    fun refresh() {
        currentPage = 0
        val currentState = _uiState.value
        if (currentState is FavoriteUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
        }
        load(page = 0, isRefresh = true)
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState is FavoriteUiState.Success &&
            !currentState.isLoadingMore &&
            currentState.hasMore) {
            load(page = currentPage + 1, isLoadMore = true)
        }
    }

    private fun load(page: Int = 0, isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        if (isLoadMore) {
            val currentState = _uiState.value
            if (currentState is FavoriteUiState.Success) {
                _uiState.value = currentState.copy(isLoadingMore = true)
            }
        } else if (!isRefresh) {
            _uiState.value = FavoriteUiState.Loading
        }

        viewModelScope.launch {
            val result = repo.getCollectList(page).first()

            when (result) {
                is Result.Success -> {
                    val pageData = result.data.data
                    val newArticles = pageData?.datas ?: emptyList()
                    totalPages = pageData?.pageCount ?: 1

                    if (isLoadMore) {
                        val currentState = _uiState.value
                        if (currentState is FavoriteUiState.Success) {
                            val allArticles = currentState.articles + newArticles
                            val hasMore = page < totalPages - 1
                            _uiState.value = FavoriteUiState.Success(
                                articles = allArticles,
                                isLoadingMore = false,
                                isRefreshing = false,
                                hasMore = hasMore
                            )
                            currentPage = page
                        }
                    } else {
                        val hasMore = page < totalPages - 1
                        _uiState.value = FavoriteUiState.Success(
                            articles = newArticles,
                            isLoadingMore = false,
                            isRefreshing = false,
                            hasMore = hasMore
                        )
                        currentPage = page
                    }
                }
                is Result.Failure -> {
                    if (isLoadMore) {
                        val currentState = _uiState.value
                        if (currentState is FavoriteUiState.Success) {
                            _uiState.value = currentState.copy(isLoadingMore = false)
                        }
                    } else if (isRefresh) {
                        val currentState = _uiState.value
                        if (currentState is FavoriteUiState.Success) {
                            _uiState.value = currentState.copy(isRefreshing = false)
                        } else {
                            _uiState.value = FavoriteUiState.Error(
                                result.exception.message ?: "刷新失败"
                            )
                        }
                    } else {
                        _uiState.value = FavoriteUiState.Error(
                            result.exception.message ?: "加载失败"
                        )
                    }
                }
            }
        }
    }
}

