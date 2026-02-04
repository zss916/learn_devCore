package com.xxx.devcore.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ssz.network.model.Banner
import kotlinx.coroutines.delay

/**
 * Banner轮播视图 - 支持无限循环和指示器
 */
@Composable
fun BannerView(
    banners: List<Banner>,
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    // 使用一个很大的数字实现无限循环
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )

    // 计算当前实际显示的banner索引
    val currentBannerIndex = remember {
        derivedStateOf {
            pagerState.currentPage % banners.size
        }
    }

    // 初始化到中间位置
    LaunchedEffect(Unit) {
        pagerState.animateScrollToPage(initialPage)
    }

    // 自动轮播
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            if (pagerState.currentPage < Int.MAX_VALUE - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val bannerIndex = page % banners.size
            val banner = banners[bannerIndex]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        banner.url?.let(onBannerClick)
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = banner.imagePath),
                    contentDescription = banner.title,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 指示器
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                val isSelected = index == currentBannerIndex.value
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (isSelected) Color(0xFF007DFF) else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

