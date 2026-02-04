package com.xxx.devcore.main.mine

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxx.devcore.compose.FlyingHeart
import com.xxx.devcore.compose.HeartAnimationState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot

/**
 * 我的页面
 */
@Composable
fun MineScreen(
    onSettingsClick: (String?) -> Unit,
    onFavoriteClick: () -> Unit,
    viewModel: MineViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 在"我的"页面按返回键直接退出应用
    BackHandler {
        (context as? ComponentActivity)?.finish()
    }

    val hearts = remember { mutableStateListOf<HeartAnimationState>() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
        // 顶部头像和登录状态区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Android 图标
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF007DFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Android",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 登录状态
                val loginText = when (val state = uiState) {
                    is MineUiState.Success -> state.nickname ?: state.username ?: "已登录"
                    is MineUiState.NotLoggedIn -> "未登录"
                    else -> "加载中..."
                }
                Text(
                    text = loginText,
                    color = Color(0xFF00C853),
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 列表项
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 我的积分
            val coinValue = when (val state = uiState) {
                is MineUiState.Success -> state.coinCount.toString()
                is MineUiState.NotLoggedIn -> "0"
                else -> "--"
            }
            MineListItem(
                title = "我的积分",
                value = coinValue,
                onClick = null
            )

            // 我的收藏
            MineListItem(
                title = "我的收藏",
                value = null,
                onClick = onFavoriteClick
            )

            // 设置
            val username = when (val state = uiState) {
                is MineUiState.Success -> state.username
                else -> null
            }
            MineListItem(
                title = "设置",
                value = null,
                onClick = { onSettingsClick(username) }
            )

            // 爱心动画
            var tryItemCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
            MineListItem(
                title = "爱心动画",
                value = "",
                onClick = {
                    // 获取点击位置并添加爱心动画
                    tryItemCoordinates?.let { coordinates ->
                        val position = coordinates.positionInRoot()
                        val centerX = position.x + coordinates.size.width / 2f
                        val centerY = position.y + coordinates.size.height / 2f
                        hearts.add(HeartAnimationState(centerX, centerY))
                    }
                },
                onGloballyPositioned = { coordinates ->
                    tryItemCoordinates = coordinates
                }
            )

            // 我的微信
            MineListItem(
                title = "我的微信",
                value = "hmssz1",
                onClick = null
            )
        }
        }
        
        // 显示所有爱心动画
        hearts.forEach { heartState ->
            FlyingHeart(
                state = heartState,
                onAnimationEnd = {
                    hearts.remove(heartState)
                }
            )
        }
    }
}

/**
 * 列表项组件
 */
@Composable
fun MineListItem(
    title: String,
    value: String?,
    onClick: (() -> Unit)?,
    onGloballyPositioned: ((LayoutCoordinates) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onGloballyPositioned != null) {
                    Modifier.onGloballyPositioned(onGloballyPositioned)
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (value != null) {
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
                if (onClick != null) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF999999)
                    )
                }
            }
        }
    }
}


