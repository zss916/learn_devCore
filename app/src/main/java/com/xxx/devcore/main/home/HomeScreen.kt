package com.xxx.devcore.main.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxx.devcore.main.ErrorMessage
import com.xxx.devcore.main.LoadingIndicator
import com.xxx.devcore.main.home.viewmodel.HomeArticleUiState
import com.xxx.devcore.main.home.viewmodel.HomeArticleViewModel
import com.xxx.devcore.compose.ArticleList

/**
 * 首页：显示文章列表
 */
@Composable
fun HomeScreen(
    viewModel: HomeArticleViewModel = viewModel(),
    onArticleClick: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val banners by viewModel.banners.collectAsState()

    // 在首页按返回键直接退出应用
    BackHandler {
        (context as? ComponentActivity)?.finish()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeArticleUiState.Loading -> LoadingIndicator()
            is HomeArticleUiState.Success -> ArticleList(
                articles = state.articles,
                onArticleClick = onArticleClick,
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.refresh() },
                isLoadingMore = state.isLoadingMore,
                hasMore = state.hasMore,
                onLoadMore = { viewModel.loadMore() },
                banners = banners,
                onBannerClick = onArticleClick
            )
            is HomeArticleUiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.refresh() }
            )
        }
    }
}



