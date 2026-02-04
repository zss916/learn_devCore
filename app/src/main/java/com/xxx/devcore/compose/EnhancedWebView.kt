package com.xxx.devcore.compose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * 增强的 WebView 组件，使用 Compose 实现
 * 
 * @param url 要加载的 URL
 * @param modifier 修饰符
 * @param onPageTitleChanged 页面标题变化回调
 * @param onLoadingStateChanged 加载状态变化回调
 * @param onError 错误回调
 * @param onCanGoBackChanged 是否可以返回上一页变化回调
 * @param onWebViewCreated WebView 创建完成回调，用于外部访问 WebView 实例
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EnhancedWebView(
    url: String,
    modifier: Modifier = Modifier,
    onPageTitleChanged: (String) -> Unit = {},
    onLoadingStateChanged: (Boolean) -> Unit = {},
    onError: (String?) -> Unit = {},
    onCanGoBackChanged: (Boolean) -> Unit = {},
    onWebViewCreated: (WebView) -> Unit = {}
) {
    val context = LocalContext.current
    
    // 使用 remember 保存 WebView 实例，避免重复创建
    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                // 允许访问文件
                allowFileAccess = true
                // 允许内容访问
                allowContentAccess = true
            }
        }
    }
    
    // 监听页面标题变化
    var pageTitle by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    
    // 通知外部 WebView 已创建
    LaunchedEffect(webView) {
        onWebViewCreated(webView)
    }
    
    // 设置 WebView 客户端
    LaunchedEffect(webView) {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
                onLoadingStateChanged(true)
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false
                onLoadingStateChanged(false)
                
                // 更新标题
                view?.title?.let { title ->
                    if (title != pageTitle) {
                        pageTitle = title
                        onPageTitleChanged(title)
                    }
                }
                
                // 更新是否可以返回
                val canGoBackState = view?.canGoBack() ?: false
                if (canGoBackState != canGoBack) {
                    canGoBack = canGoBackState
                    onCanGoBackChanged(canGoBackState)
                }
            }
            
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                isLoading = false
                onLoadingStateChanged(false)
                onError(error?.description?.toString())
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    isLoading = false
                    onLoadingStateChanged(false)
                }
                
                // 更新是否可以返回
                val canGoBackState = view?.canGoBack() ?: false
                if (canGoBackState != canGoBack) {
                    canGoBack = canGoBackState
                    onCanGoBackChanged(canGoBackState)
                }
            }
            
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                title?.let {
                    if (it != pageTitle) {
                        pageTitle = it
                        onPageTitleChanged(it)
                    }
                }
            }
        }
        
        // 加载 URL
        if (url.isNotBlank()) {
            webView.loadUrl(url)
        }
    }
    
    // 当 URL 变化时重新加载
    LaunchedEffect(url) {
        if (url.isNotBlank() && webView.url != url) {
            webView.loadUrl(url)
        }
    }
    
    // 清理 WebView 资源
    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }
    
    // 渲染 WebView
    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}

