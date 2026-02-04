package com.ssz.network.repository

import com.ssz.network.model.ArticlePage
import com.ssz.network.model.Banner
import com.ssz.network.model.BaseResponse
import com.ssz.network.model.NavCategory
import com.ssz.network.Result
import com.ssz.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * 首页文章仓库
 */
class ArticleRepository {
    private val api = RetrofitInstance.wanAndroidApiService

    /**
     * 获取首页文章列表
     * @param page 页码，从0开始
     * @param pageSize 可选，1-40，不传则用后端默认
     */
    fun getHomeArticles(
        page: Int,
        pageSize: Int? = null
    ): Flow<Result<BaseResponse<ArticlePage>>> = flow {
        val resp = api.getHomeArticles(page, pageSize)
        if (resp.errorCode == 0) {
            ///todo 发射
            emit(Result.Success(resp))
        } else {
            emit(Result.Failure(Exception(resp.errorMsg.ifEmpty { "获取文章失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "获取文章失败")))
    }

    /**
     * 获取广场文章列表
     * @param page 页码，从0开始
     * @param pageSize 可选，1-40，不传则用后端默认
     */
    fun getSquareArticles(
        page: Int,
        pageSize: Int? = null
    ): Flow<Result<BaseResponse<ArticlePage>>> = flow {
        val resp = api.getSquareArticles(page, pageSize)
        if (resp.errorCode == 0) {
            emit(Result.Success(resp))
        } else {
            emit(Result.Failure(Exception(resp.errorMsg.ifEmpty { "获取广场文章失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "获取广场文章失败")))
    }

    /**
     * 获取首页Banner
     */
    fun getBanners(): Flow<Result<BaseResponse<List<Banner>>>> = flow {
        val resp = api.getBanners()
        if (resp.errorCode == 0) {
            emit(Result.Success(resp))
        } else {
            emit(Result.Failure(Exception(resp.errorMsg.ifEmpty { "获取Banner失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "获取Banner失败")))
    }

    /**
     * 获取导航数据
     */
    fun getNavigation(): Flow<Result<BaseResponse<List<NavCategory>>>> = flow {
        val resp = api.getNavigation()
        if (resp.errorCode == 0) {
            emit(Result.Success(resp))
        } else {
            emit(Result.Failure(Exception(resp.errorMsg.ifEmpty { "获取导航数据失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "获取导航数据失败")))
    }

    /**
     * 获取收藏列表
     * @param page 页码，从0开始
     * @param pageSize 可选，1-40，不传则使用默认值
     * 注：一旦传入了 page_size，后续该接口分页都需要带上，否则会造成分页读取错误
     */
    fun getCollectList(
        page: Int,
        pageSize: Int? = null
    ): Flow<Result<BaseResponse<ArticlePage>>> = flow {
        val resp = api.getCollectList(page, pageSize)
        if (resp.errorCode == 0) {
            emit(Result.Success(resp))
        } else {
            emit(Result.Failure(Exception(resp.errorMsg.ifEmpty { "获取收藏列表失败" })))
        }
    }.catch { e ->
        emit(Result.Failure(e as? Exception ?: Exception(e.message ?: "获取收藏列表失败")))
    }

    /**
     * 收藏站内文章
     * @param id 文章id，拼接在链接中
     */
    suspend fun collect(id: Int): Result<BaseResponse<Any>> {
        return try {
            val resp = api.collect(id)
            if (resp.errorCode == 0) {
                Result.Success(resp)
            } else {
                Result.Failure(Exception(resp.errorMsg.ifEmpty { "收藏失败" }))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * 收藏站外文章
     * @param title 文章标题
     * @param author 作者
     * @param link 文章链接
     */
    suspend fun collectExternal(
        title: String,
        author: String,
        link: String
    ): Result<BaseResponse<Any>> {
        return try {
            val resp = api.collectExternal(title, author, link)
            if (resp.errorCode == 0) {
                Result.Success(resp)
            } else {
                Result.Failure(Exception(resp.errorMsg.ifEmpty { "收藏站外文章失败" }))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


    /**
     * 取消收藏（文章列表）
     * @param id 文章id，传入的是列表中文章的id
     */
    suspend fun unCollect(id: Int): Result<BaseResponse<Any>> {
        return try {
            val resp = api.unCollect(id)
            if (resp.errorCode == 0) {
                Result.Success(resp)
            } else {
                Result.Failure(Exception(resp.errorMsg.ifEmpty { "取消收藏失败" }))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}

///todo 学习网址： https://blog.csdn.net/xuyin1204/article/details/155915216
///todo 流 发射
/*
val xxxFlow =  flow{
    delay(1000)
    emit(xxxxx)
}*/

///todo 流 接受
/*
lifecycleScope.launch{
    xxxFlow.collect{ value ->
        Log.d("xxxx","xxxxxx")
    }
}*/
