package com.ssz.network.repository

import com.ssz.network.model.BaseResponse
import com.ssz.network.Result
import com.ssz.network.RetrofitInstance
import com.ssz.network.model.UserCoinData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UserCoinRepository {
    private val apiService = RetrofitInstance.wanAndroidApiService

    /**
     * 积分信息
     */
    fun getUserCoinInfo(): Flow<Result<BaseResponse<UserCoinData>>> = flow {
        val response = apiService.getUserCoinInfo()
        if (response.errorCode == 0 && response.data != null) {
            emit(Result.Success(response))
        } else {
            emit(Result.Failure(Exception(response.errorMsg.ifEmpty { "获取积分信息失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "获取积分信息失败")))
    }
}