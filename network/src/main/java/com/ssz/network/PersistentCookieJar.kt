package com.ssz.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

/**
 * Cookie持久化存储实现
 */
class PersistentCookieJar(context: Context) : CookieJar {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "CookiePrefs",
        Context.MODE_PRIVATE
    )
    
    private val cookies: MutableMap<String, MutableList<Cookie>> = ConcurrentHashMap()
    
    init {
        // 从SharedPreferences加载已保存的cookies
        loadCookies()
    }
    
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val cookiesForHost = cookies[host] ?: emptyList()
        
        // 过滤掉过期的cookies
        val validCookies = cookiesForHost.filter { cookie ->
            cookie.expiresAt == -1L || cookie.expiresAt > System.currentTimeMillis()
        }
        
        // 更新存储
        if (validCookies.size != cookiesForHost.size) {
            cookies[host] = validCookies.toMutableList()
            saveCookies()
        }
        
        return validCookies
    }
    
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val existingCookies = this.cookies[host]?.toMutableList() ?: mutableListOf()
        
        // 更新或添加cookies
        cookies.forEach { cookie ->
            // 移除同名的旧cookie
            existingCookies.removeAll { it.name == cookie.name && it.domain == cookie.domain }
            // 添加新cookie
            existingCookies.add(cookie)
        }
        
        this.cookies[host] = existingCookies
        saveCookies()
    }
    
    /**
     * 保存cookies到SharedPreferences
     */
    private fun saveCookies() {
        val editor = prefs.edit()
        editor.clear()
        cookies.forEach { (host, cookieList) ->
            val cookieStrings = cookieList.map { cookieToString(it) }
            editor.putStringSet(host, cookieStrings.toSet())
        }

        editor.apply()
    }
    
    /**
     * 从SharedPreferences加载cookies
     */
    private fun loadCookies() {
        val allEntries = prefs.all
        allEntries.forEach { (host, value) ->
            if (value is Set<*>) {
                val cookieList = value.mapNotNull { stringToCookie(it as String) }
                cookies[host] = cookieList.toMutableList()
            }
        }
    }
    
    /**
     * 将Cookie转换为字符串
     */
    private fun cookieToString(cookie: Cookie): String {
        return buildString {
            append(cookie.name)
            append("=")
            append(cookie.value)
            append(";")
            append("domain=")
            append(cookie.domain)
            append(";")
            append("path=")
            append(cookie.path)
            append(";")
            if (cookie.expiresAt != -1L) {
                append("expiresAt=")
                append(cookie.expiresAt)
                append(";")
            }
            if (cookie.secure) {
                append("secure;")
            }
            if (cookie.httpOnly) {
                append("httpOnly;")
            }
            if (cookie.hostOnly) {
                append("hostOnly;")
            }
        }
    }
    
    /**
     * 将字符串转换为Cookie
     */
    private fun stringToCookie(cookieString: String): Cookie? {
        return try {
            val parts = cookieString.split(";")
            if (parts.isEmpty()) return null
            
            val nameValue = parts[0].split("=")
            if (nameValue.size != 2) return null
            
            val name = nameValue[0].trim()
            val value = nameValue[1].trim()
            
            var domain = ""
            var path = "/"
            var expiresAt = -1L
            var secure = false
            var httpOnly = false
            var hostOnly = false
            
            parts.drop(1).forEach { part ->
                val trimmed = part.trim()
                when {
                    trimmed.startsWith("domain=") -> domain = trimmed.substring(7)
                    trimmed.startsWith("path=") -> path = trimmed.substring(5)
                    trimmed.startsWith("expiresAt=") -> expiresAt = trimmed.substring(10).toLongOrNull() ?: -1L
                    trimmed == "secure" -> secure = true
                    trimmed == "httpOnly" -> httpOnly = true
                    trimmed == "hostOnly" -> hostOnly = true
                }
            }
            
            val builder = Cookie.Builder()
                .name(name)
                .value(value)
                .path(path)
                .apply {
                    if (expiresAt != -1L) {
                        expiresAt(expiresAt)
                    }
                    if (secure) {
                        secure()
                    }
                    if (httpOnly) {
                        httpOnly()
                    }
                }
            
            // hostOnly 属性由 domain 的设置方式决定
            // 如果 hostOnly 为 true，domain 不应该以 "." 开头
            // 如果 hostOnly 为 false，domain 应该以 "." 开头
            if (hostOnly) {
                // hostOnly cookie: domain 不应该以 "." 开头
                builder.domain(domain.removePrefix("."))
            } else {
                // 非 hostOnly cookie: domain 应该以 "." 开头（如果还没有的话）
                builder.domain(if (domain.startsWith(".")) domain else ".$domain")
            }
            
            builder.build()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 清除所有cookies
     */
    fun clearCookies() {
        cookies.clear()
        prefs.edit().clear().apply()
    }
}

