package com.xxx.devcore.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ssz.network.model.Article

/**
 * 广场文章列表
 */
@Composable
fun SquareArticleList(
    articles: List<Article>,
    onArticleClick: (String) -> Unit,
    onCollectClick: (Article, Boolean) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(articles) { article ->
            SquareArticleCard(
                article = article,
                onArticleClick = onArticleClick,
                onCollectClick = { isCollect ->
                    onCollectClick(article, isCollect)
                }
            )
        }
    }
}

/**
 * 广场文章 item - 标题、时间在左边，收藏按钮在右边
 */
@Composable
fun SquareArticleCard(
    article: Article,
    onArticleClick: (String) -> Unit,
    onCollectClick: (Boolean) -> Unit
) {
    val link = article.link
    val isCollected = article.collect ?: false
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(link) {
                if (link.isNullOrBlank()) return@pointerInput
                detectTapGestures {
                    link.let(onArticleClick)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题占据整行，固定两行高度
            Text(
                text = article.title ?: "无标题",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            // 时间和标题之间的间距
            Spacer(modifier = Modifier.height(8.dp))
            // 时间在左边，收藏按钮在右边
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 时间显示在左边
                article.niceDate?.let { date ->
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // 收藏按钮在右边
                Icon(
                    imageVector = if (isCollected) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isCollected) "取消收藏" else "收藏",
                    tint = if (isCollected) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onCollectClick(!isCollected)
                        }
                )
            }
        }
    }
}

