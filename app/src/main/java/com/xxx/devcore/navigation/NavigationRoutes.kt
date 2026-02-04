package com.xxx.devcore.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * 导航路由定义
 */
object NavigationRoutes {
    // 主Tab路由（与TabItem保持一致）
    const val HOME = "home"
    const val SQUARE = "square"
    const val NAVIGATION = "navigation"
    const val MINE = "mine"
    
    // 其他页面路由
    const val SETTINGS = "settings"
    const val FAVORITE = "favorite"
    const val USER_INFO = "user_info"
    const val WEBVIEW = "webview"
    
    // 带参数的路由模板
    const val SETTINGS_WITH_USERNAME = "$SETTINGS/{username}"
    const val USER_INFO_WITH_DATA = "$USER_INFO/{userDataJson}"
    const val WEBVIEW_WITH_URL = "$WEBVIEW/{url}"
    
    /**
     * 构建设置页面路由
     * @param username 用户名，如果为null或空则使用无参数路由
     */
    fun settingsRoute(username: String? = null): String {
        return if (username.isNullOrEmpty()) {
            SETTINGS
        } else {
            "$SETTINGS/$username"
        }
    }
    
    /**
     * 获取设置页面的导航参数定义
     */
    fun settingsArguments() = listOf(
        navArgument("username") {
            type = NavType.StringType
            defaultValue = ""
            nullable = true
        }
    )
    
    /**
     * 构建用户信息页面路由
     * @param userDataJson 用户数据的JSON字符串
     */
    fun userInfoRoute(userDataJson: String): String {
        return "$USER_INFO/$userDataJson"
    }
    
    /**
     * 获取用户信息页面的导航参数定义
     */
    fun userInfoArguments() = listOf(
        navArgument("userDataJson") {
            type = NavType.StringType
            defaultValue = ""
        }
    )
    
    /**
     * 构建 WebView 页面路由
     * @param url 要加载的 URL，会自动进行 URL 编码
     */
    fun webviewRoute(url: String): String {
        val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
        return "$WEBVIEW/$encodedUrl"
    }
    
    /**
     * 获取 WebView 页面的导航参数定义
     */
    fun webviewArguments() = listOf(
        navArgument("url") {
            type = NavType.StringType
            defaultValue = ""
        }
    )
}

