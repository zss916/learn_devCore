package com.ssz.network.model

/**
 * 用户积分数据
 */
data class UserCoinData(
    val coinCount: Int, // 积分
    val level: Int, // 等级
    val nickname: String?, // 昵称
    val rank: String?, // 排名
    val userId: Int, // 用户ID
    val username: String? // 用户名
)

