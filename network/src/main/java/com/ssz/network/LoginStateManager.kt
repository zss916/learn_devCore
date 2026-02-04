package com.ssz.network

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 登录状态管理
 */
object LoginStateManager {
    private const val PREFS_NAME = "LoginStatePrefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    // 登录状态变化通知
    private val _loginStateChanged = MutableStateFlow<Boolean?>(null)
    val loginStateChanged: StateFlow<Boolean?> = _loginStateChanged.asStateFlow()

    /**
     * 检查是否已登录（通过检查Cookie是否存在）
     */
    fun isLoggedIn(context: Context): Boolean {
        val cookiePrefs = context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)
        val allEntries = cookiePrefs.all

        // 检查是否有wanandroid.com的cookie
        return allEntries.any { (host, _) ->
            host.contains("wanandroid.com") && cookiePrefs.getStringSet(host, null)?.isNotEmpty() == true
        }
    }

    /**
     * 保存登录状态
     */
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
        _loginStateChanged.value = isLoggedIn
    }

    /**
     * 清除登录状态
     */
    fun clearLoginState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_IS_LOGGED_IN).apply()
        // 清除 Cookie
        val cookiePrefs = context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)
        cookiePrefs.edit().clear().apply()
        // 通知登录状态变化
        _loginStateChanged.value = false
    }

    /**
     * 通知认证错误（通过 Flow 发送消息）
     * 当检测到 errorCode == -1001 时调用此方法
     */
    fun notifyAuthError() {
        // 通过 Flow 发送登录状态变化消息（false 表示未登录）
        _loginStateChanged.value = false
    }
}