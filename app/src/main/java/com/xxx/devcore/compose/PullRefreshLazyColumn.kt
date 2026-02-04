package com.xxx.devcore.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.pulltorefresh.*
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.pullrefresh.PullRefreshIndicator
//import androidx.compose.material3.pulltorefresh.pullToRefresh
//import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


/**
 * material3 不支持下拉刷新pullrefresh
 *
 * 可下拉刷新和上拉加载更多的 LazyColumn
 * 
 * @param isRefreshing 是否正在刷新
 * @param onRefresh 刷新回调
 * @param isLoadingMore 是否正在加载更多
 * @param hasMore 是否还有更多数据
 * @param onLoadMore 加载更多回调
 * @param contentPadding 内容内边距
 * @param listState LazyListState，如果不提供则使用 rememberLazyListState()
 * @param modifier Modifier
 * @param content LazyListScope 内容
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefreshLazyColumn(
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    isLoadingMore: Boolean = false,
    hasMore: Boolean = true,
    onLoadMore: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )


    // 监听滚动，接近底部时加载更多
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == layoutInfo.totalItemsCount - 1
        }.collect { isAtBottom ->
            if (isAtBottom && hasMore && !isLoadingMore && !isRefreshing) {
                onLoadMore()
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)

    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            content = {
                // 调用传入的内容
                content()
                
                // 加载更多指示器（自动添加）
                if (isLoadingMore) {
                    item {
                        LoadMoreIndicator()
                    }
                }
            }
        )
        
        // 下拉刷新指示器
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * 加载更多指示器组件
 */
@Composable
fun LoadMoreIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

