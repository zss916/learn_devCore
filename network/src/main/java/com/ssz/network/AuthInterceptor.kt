package com.ssz.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssz.network.model.BaseResponse
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * 认证拦截器：检查响应中的 errorCode，如果是 -1001 则触发登录跳转
 */
class AuthInterceptor: Interceptor {
    
    private val gson = Gson()
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // 只处理成功的响应（200-299）
        if (response.isSuccessful) {
            val responseBody = response.body
            if (responseBody != null) {
                try {
                    // 读取响应体
                    val source = responseBody.source()
                    source.request(Long.MAX_VALUE) // 读取整个响应体
                    val buffer = source.buffer
                    val responseBodyString = buffer.clone().readUtf8()
                    
                    // 使用统一的 BaseResponse 解析 JSON 检查 errorCode
                    // 使用 TypeToken 来正确解析泛型类型
                    val type = object : TypeToken<BaseResponse<Any>>() {}.type
                    val baseResponse = gson.fromJson<BaseResponse<Any>>(responseBodyString, type)
                    
                    // 如果是 -1001，触发登录跳转
                    if (baseResponse.needLogin()) {
                        Log.d("sszLog:", "检测到 errorCode == -1001，通过 Flow 发送登录状态变化消息")
                        // 通过 Flow 发送消息，通知 app 登录状态变化并跳转登录页面
                        LoginStateManager.notifyAuthError()
                    }
                    
                    // 重新创建响应体（因为已经读取过了）
                    val newResponseBody = responseBodyString.toResponseBody(responseBody.contentType())
                    return response.newBuilder()
                        .body(newResponseBody)
                        .build()
                } catch (e: Exception) {
                    Log.e("sszLog:", "解析响应体失败: ${e.message}")
                    // 如果解析失败，返回原始响应
                    return response
                }
            }
        }
        
        return response
    }
}

