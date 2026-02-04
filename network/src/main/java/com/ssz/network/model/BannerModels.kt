package com.ssz.network.model

/**
 * Banner数据
 */
data class Banner(
    val id: Int,
    val desc: String?,
    val imagePath: String?,
    val isVisible: Int,
    val order: Int,
    val title: String?,
    val type: Int,
    val url: String?
)

