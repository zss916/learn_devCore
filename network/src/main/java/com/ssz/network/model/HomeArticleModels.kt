package com.ssz.network.model

/**
 * 分页信息
 */
data class ArticlePage(
    val curPage: Int,
    val datas: List<Article>,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

/**
 * 文章数据
 */
data class Article(
    val id: Long,
    val title: String?,
    val link: String?,
    val author: String?,
    val shareUser: String?,
    val niceDate: String?,
    val superChapterName: String?,
    val chapterName: String?,
    val desc: String?,
    val envelopePic: String?,
    val collect: Boolean?,
    val publishTime: Long?
)


