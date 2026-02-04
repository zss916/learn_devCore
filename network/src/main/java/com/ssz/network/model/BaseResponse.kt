package com.ssz.network.model

/**
 * 统一的网络响应体基类
 * 所有 API 响应都遵循此格式：
 * {
 *     "data": ...,
 *     "errorCode": 0,
 *     "errorMsg": ""
 * }
 *
 * @param T data 字段的类型
 */
data class BaseResponse<T>(
    val data: T?,
    val errorCode: Int,
    val errorMsg: String
) {
    /**
     * 判断请求是否成功
     */
    fun isSuccess(): Boolean = errorCode == 0

    /**
     * 判断是否需要登录（errorCode == -1001）
     */
    fun needLogin(): Boolean = errorCode == -1001
}