package com.ssz.network.repository

import com.ssz.network.model.BaseResponse
import com.ssz.network.Result
import com.ssz.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RegisterRepository {
    private val apiService = RetrofitInstance.wanAndroidApiService

    /**
     * 用户注册
     */
    fun register(
        username: String,
        password: String,
        rePassword: String
    ): Flow<Result<BaseResponse<Any>>> = flow {
        val response = apiService.register(username, password, rePassword)
        if (response.errorCode == 0) {
            emit(Result.Success(response))
        } else {
            emit(Result.Failure(Exception(response.errorMsg.ifEmpty { "注册失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "注册失败")))
    }
}