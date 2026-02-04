package com.xxx.devcore.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
//import androidx.compose.material3.icon.Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * 爱心动画状态
 */
data class HeartAnimationState(
    val startX: Float,
    val startY: Float,
    val id: Long = System.currentTimeMillis()
)

/**
 * 飞出的爱心动画
 */
@Composable
fun FlyingHeart(
    state: HeartAnimationState,
    onAnimationEnd: () -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val offsetX = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(state) {
        // 随机横向偏移
        val randomX = (Random.nextFloat() - 0.5f) * 200f
        // 向上飞出
        val targetY = -400f
        
        // 同时执行多个动画
        coroutineScope {
            launch {
                offsetY.animateTo(
                    targetValue = targetY,
                    animationSpec = tween<Float>(1000)
                )
            }
            launch {
                offsetX.animateTo(
                    targetValue = randomX,
                    animationSpec = tween<Float>(1000)
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween<Float>(1000)
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1.5f,
                    animationSpec = tween<Float>(1000)
                )
            }
        }
        
        // 动画结束后回调
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (state.startX + offsetX.value).roundToInt(),
                    y = (state.startY + offsetY.value).roundToInt()
                )
            }
            .size(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.Red.copy(alpha = alpha.value),
            modifier = Modifier.size((24.dp * scale.value))
        )
    }
}

