package com.ssz.network

import com.ssz.network.model.ArticlePage
import com.ssz.network.model.Banner
import com.ssz.network.model.BaseResponse
import com.ssz.network.model.NavCategory
import com.ssz.network.model.UserCoinData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 玩Android API服务
 */
interface ApiService {
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param repassword 确认密码
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") rePassword: String
    ): BaseResponse<Any>
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<Any>

    /**
     * 用户退出
     */
    @GET("user/logout/json")
    suspend fun logout(): BaseResponse<Any>

    /**
     * 首页文章列表
     * page 从0开始，pageSize 可选（1-40）
     */
    @GET("article/list/{page}/json")
    suspend fun getHomeArticles(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): BaseResponse<ArticlePage>

    /**
     * 获取用户积分信息
     */
    @GET("lg/coin/userinfo/json")
    suspend fun getUserCoinInfo(): BaseResponse<UserCoinData>

    /**
     * 广场文章列表
     * page 从0开始，pageSize 可选（1-40）
     */
    @GET("user_article/list/{page}/json")
    suspend fun getSquareArticles(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): BaseResponse<ArticlePage>

    /**
     * 获取首页Banner
     */
    @GET("banner/json")
    suspend fun getBanners(): BaseResponse<List<Banner>>

    /**
     * 获取导航数据
     */
    @GET("navi/json")
    suspend fun getNavigation(): BaseResponse<List<NavCategory>>

    /**
     * 获取收藏列表
     * @param page 页码，从0开始，拼接在链接中
     * @param pageSize 可选，1-40，不传则使用默认值
     * 注：一旦传入了 page_size，后续该接口分页都需要带上，否则会造成分页读取错误
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): BaseResponse<ArticlePage>

    /**
     * 收藏站内文章
     * @param id 文章id，拼接在链接中
     */
    @POST("lg/collect/{id}/json")
    suspend fun collect(
        @Path("id") id: Int
    ): BaseResponse<Any>

    /**
     * 收藏站外文章
     * @param title 文章标题
     * @param author 作者
     * @param link 文章链接
     */
    @FormUrlEncoded
    @POST("lg/collect/add/json")
    suspend fun collectExternal(
        @Field("title") title: String,
        @Field("author") author: String,
        @Field("link") link: String
    ): BaseResponse<Any>

    /**
     * 取消收藏（文章列表）
     * @param id 文章id，拼接在链接中，传入的是列表中文章的id
     */
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun unCollect(
        @Path("id") id: Int
    ): BaseResponse<Any>
}

