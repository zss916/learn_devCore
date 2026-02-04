package com.xxx.devcore.main.webview

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.xxx.devcore.compose.EnhancedWebView

/**
 * WebView 页面屏幕，完全使用 Compose 实现
 * 
 * @param url 要加载的 URL
 * @param onBack 返回按钮点击回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPageScreen(
    url: String,
    onBack: () -> Unit
) {
    var pageTitle by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (pageTitle.isNotEmpty()) {
                        Text(
                            text = pageTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp
                        )
                    } else {
                        Text(
                            text = "加载中...",
                            fontSize = 16.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (canGoBack) {
                                webView?.goBack()
                            } else {
                                onBack()
                            }
                        },
                        enabled = canGoBack || isLoading.not()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (canGoBack) "返回上一页" else "关闭"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { webView?.reload() }) {
                        Icon(Icons.Default.Refresh, "刷新")
                    }
                    IconButton(onClick = { /* 分享功能待实现 */ }) {
                        Icon(Icons.Default.Share, "分享")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            EnhancedWebView(
                url = url,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onPageTitleChanged = { title ->
                    pageTitle = title
                },
                onLoadingStateChanged = { loading ->
                    isLoading = loading
                },
                onError = { error ->
                    // 可以在这里显示错误页面
                },
                onCanGoBackChanged = { canGoBackState ->
                    canGoBack = canGoBackState
                },
                onWebViewCreated = { view ->
                    webView = view
                }
            )
            
            // 自定义进度条
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
                
                // 加载指示器显示在屏幕中间
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

