package com.xxx.devcore.navigation

import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.google.gson.Gson
import com.ssz.network.model.UserCoinData
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * 导航扩展函数，提供便捷的导航方法
 */

/**
 * 导航到设置页面
 */
fun NavController.navigateToSettings(username: String? = null) {
    navigate(NavigationRoutes.settingsRoute(username))
}

/**
 * 导航到收藏页面
 */
fun NavController.navigateToFavorite() {
    navigate(NavigationRoutes.FAVORITE)
}

/**
 * 导航到用户信息页面
 * @param userData 用户数据实体类
 */
fun NavController.navigateToUserInfo(userData: UserCoinData) {
    val gson = Gson()
    val json = gson.toJson(userData)
    // URL编码，避免特殊字符导致路由解析错误
    val encodedJson = URLEncoder.encode(json, "UTF-8")
    navigate(NavigationRoutes.userInfoRoute(encodedJson))
}

/**
 * 从导航参数中获取用户名
 */
fun NavBackStackEntry.getUsername(): String? {
    val username = arguments?.getString("username")
    return if (username.isNullOrEmpty()) null else username
}

/**
 * 从导航参数中获取用户数据
 */
fun NavBackStackEntry.getUserData(): UserCoinData? {
    val encodedJson = arguments?.getString("userDataJson") ?: return null
    return try {
        val json = URLDecoder.decode(encodedJson, "UTF-8")
        val gson = Gson()
        gson.fromJson(json, UserCoinData::class.java)
    } catch (e: Exception) {
        null
    }
}

/**
 * 导航到 WebView 页面
 * @param url 要加载的 URL
 */
fun NavController.navigateToWebView(url: String) {
    navigate(NavigationRoutes.webviewRoute(url))
}

/**
 * 从导航参数中获取 URL
 */
fun NavBackStackEntry.getWebViewUrl(): String {
    val encodedUrl = arguments?.getString("url") ?: return ""
    return try {
        URLDecoder.decode(encodedUrl, "UTF-8")
    } catch (e: Exception) {
        encodedUrl
    }
}

