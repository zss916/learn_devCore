package com.xxx.devcore.main.mine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xxx.devcore.main.ErrorMessage
import com.xxx.devcore.main.mine.viewmodel.FavoriteUiState
import com.xxx.devcore.main.mine.viewmodel.FavoriteViewModel
import com.xxx.devcore.main.LoadingIndicator
import com.xxx.devcore.navigation.navigateToWebView
import com.xxx.devcore.compose.ArticleList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onBack: () -> Unit,
    viewModel: FavoriteViewModel = viewModel(),
    navController: NavController? = null
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的收藏") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is FavoriteUiState.Loading -> LoadingIndicator()
                is FavoriteUiState.Success -> {
                    if (state.articles.isEmpty()) {
                        EmptyState(
                            message = "暂无收藏",
                            onRefresh = { viewModel.refresh() }
                        )
                    } else {
                        ArticleList(
                            articles = state.articles,
                            banners = emptyList(),
                            onArticleClick = { link ->
                                navController?.navigateToWebView(link)
                            },
                            isRefreshing = state.isRefreshing,
                            onRefresh = { viewModel.refresh() },
                            isLoadingMore = state.isLoadingMore,
                            hasMore = state.hasMore,
                            onLoadMore = { viewModel.loadMore() }
                        )
                    }
                }
                is FavoriteUiState.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.refresh() }
                )
            }
        }
    }
}

/**
 * 空数据页面
 */
@Composable
fun EmptyState(
    message: String = "暂无数据",
    onRefresh: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        if (onRefresh != null) {
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = onRefresh) {
                Text("刷新")
            }
        }
    }
}

