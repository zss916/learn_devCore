package com.ssz.network.repository

import com.ssz.network.model.BaseResponse
import com.ssz.network.Result
import com.ssz.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class LoginRepository {
    private val apiService = RetrofitInstance.wanAndroidApiService

    /**
     * 用户登录
     */
    fun login(
        username: String,
        password: String
    ): Flow<Result<BaseResponse<Any>>> = flow {
        val response = apiService.login(username, password)
        if (response.errorCode == 0) {
            emit(Result.Success(response))
        } else {
            emit(Result.Failure(Exception(response.errorMsg.ifEmpty { "登录失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "登录失败")))
    }

    /**
     * 用户退出
     */
    fun logout(): Flow<Result<BaseResponse<Any>>> = flow {
        val response = apiService.logout()
        if (response.errorCode == 0) {
            emit(Result.Success(response))
        } else {
            emit(Result.Failure(Exception(response.errorMsg.ifEmpty { "退出失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "退出失败")))
    }
}