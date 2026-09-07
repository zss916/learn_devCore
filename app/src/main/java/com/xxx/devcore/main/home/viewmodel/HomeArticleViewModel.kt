package com.xxx.devcore.main.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssz.network.model.Article
import com.ssz.network.model.Banner
import com.ssz.network.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ssz.network.Result
import kotlinx.coroutines.flow.update

///todo UiState
sealed class HomeArticleUiState {
    ///todo loading
    object Loading : HomeArticleUiState()
    ///todo success
    data class Success(
        val articles: List<Article>,
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false,
        val hasMore: Boolean = true
    ) : HomeArticleUiState()
    ///todo error
    data class Error(val message: String) : HomeArticleUiState()
}

///todo viewModel
class HomeArticleViewModel(
    private val repo: ArticleRepository = ArticleRepository()
) : ViewModel() {

    ///todo uiState
    private val _uiState = MutableStateFlow<HomeArticleUiState>(HomeArticleUiState.Loading)
    val uiState: StateFlow<HomeArticleUiState> = _uiState.asStateFlow()

    ///todo
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private var currentPage = 0
    private var pageSize: Int? = null
    private var totalPages = 1

    init {
        load()
        loadBanners()
    }

    fun refresh() {
        currentPage = 0
        val currentState = _uiState.value
        if (currentState is HomeArticleUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
           // _uiState.update {  }
        }
        load(page = 0, pageSize = pageSize, isRefresh = true)
        loadBanners()
    }

    private fun loadBanners() {
        viewModelScope.launch {
            repo.getBanners().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _banners.value = result.data.data ?: emptyList()
                    }
                    is Result.Failure -> {
                        // Banner加载失败不影响主页面，静默处理
                    }
                }
            }
        }
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState is HomeArticleUiState.Success && 
            !currentState.isLoadingMore && 
            currentState.hasMore) {
            load(page = currentPage + 1, pageSize = pageSize, isLoadMore = true)
        }
    }

    private fun load(page: Int = 0, pageSize: Int? = null, isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        if (isLoadMore) {
            val currentState = _uiState.value
            if (currentState is HomeArticleUiState.Success) {
                _uiState.value = currentState.copy(isLoadingMore = true)
            }
        } else if (isRefresh) {
            // 刷新时保持当前列表，只设置刷新状态
            val currentState = _uiState.value
            if (currentState is HomeArticleUiState.Success) {
                // 已经在 refresh() 方法中设置了 isRefreshing = true，这里不需要再设置
            } else {
                // 如果当前不是 Success 状态，则显示加载状态
                _uiState.value = HomeArticleUiState.Loading
            }
        } else {
            // 初始加载时才显示 Loading
            _uiState.value = HomeArticleUiState.Loading
        }
        
       val job = viewModelScope.launch {
            repo.getHomeArticles(page, pageSize).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val pageData = result.data.data
                        val newArticles = pageData?.datas ?: emptyList()
                        totalPages = pageData?.pageCount ?: 1
                        
                        if (isLoadMore) {
                            val currentState = _uiState.value
                            if (currentState is HomeArticleUiState.Success) {
                                val allArticles = currentState.articles + newArticles
                                val hasMore = page < totalPages - 1
                                _uiState.value = HomeArticleUiState.Success(
                                    articles = allArticles,
                                    isLoadingMore = false,
                                    isRefreshing = false,
                                    hasMore = hasMore
                                )
                                currentPage = page
                            }
                        } else {
                            val hasMore = page < totalPages - 1
                            _uiState.value = HomeArticleUiState.Success(
                                articles = newArticles,
                                isLoadingMore = false,
                                isRefreshing = false,
                                hasMore = hasMore
                            )
                            currentPage = page
                            this@HomeArticleViewModel.pageSize = pageSize
                        }
                    }
                    is Result.Failure -> {
                        if (isLoadMore) {
                            val currentState = _uiState.value
                            if (currentState is HomeArticleUiState.Success) {
                                _uiState.value = currentState.copy(isLoadingMore = false)
                            }
                        } else if (isRefresh) {
                            // 刷新失败时，保持当前列表，只取消刷新状态
                            val currentState = _uiState.value
                            if (currentState is HomeArticleUiState.Success) {
                                _uiState.value = currentState.copy(isRefreshing = false)
                            } else {
                                _uiState.value = HomeArticleUiState.Error(
                                    result.exception.message ?: "刷新失败"
                                )
                            }
                        } else {
                            _uiState.value = HomeArticleUiState.Error(
                                result.exception.message ?: "加载失败"
                            )
                        }
                    }
                }
            }
        }
        //job.cancel()
    }
}

///todo 接口串行合流
/*flowOf(
flow {
    emit(1)
    emit(2)
},
flow { emit(3) },
flow { emit(4) },
).flattenConcat().collect {
    Log.v("ttaylor", "${it}") // 1,2,3,4
}
只有消费了前一个流中所有的数据后才会消费后一个流
*/

///todo 接口并行合流
/*flowOf(
flow { emit(1) },
flow { emit(2) },
flow { emit(3) },
).flattenMerge().map {
    Log.v("ttaylor", "${it}")
}
*/

