package com.xxx.devcore.main.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssz.network.model.NavArticle
import com.ssz.network.model.NavCategory
import androidx.navigation.NavController
import com.xxx.devcore.main.ErrorMessage
import com.xxx.devcore.main.LoadingIndicator
import com.xxx.devcore.main.navigation.viewmodel.NavigationUiState
import com.xxx.devcore.main.navigation.viewmodel.NavigationViewModel
import com.xxx.devcore.navigation.navigateToWebView

/**
 * 导航页面
 */
@Composable
fun NavigationScreen(
    viewModel: NavigationViewModel = viewModel(),
    navController: NavController? = null
) {
    val context = LocalContext.current
    
    // 在导航页面按返回键直接退出应用
    BackHandler {
        (context as? ComponentActivity)?.finish()
    }
    
    val uiState by viewModel.uiState.collectAsState()
    ///菜单是否显示
    var isMenuVisible by rememberSaveable { mutableStateOf(true) }
    ///是否选择选中分类
    var selectedCategoryIndex by rememberSaveable { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is NavigationUiState.Loading -> LoadingIndicator()
            is NavigationUiState.Success -> {
                if (state.categories.isNotEmpty()) {
                    NavigationContent(
                        categories = state.categories,
                        isMenuVisible = isMenuVisible,
                        onMenuToggle = { isMenuVisible = !isMenuVisible },
                        selectedCategoryIndex = selectedCategoryIndex,
                        onCategorySelected = { selectedCategoryIndex = it },
                        onArticleClick = { link ->
                            navController?.navigateToWebView(link)
                        }
                    )
                }
            }
            is NavigationUiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.load() }
            )
        }
    }
}

/**
 * 导航内容布局
 */
@Composable
fun NavigationContent(
    categories: List<NavCategory>,
    isMenuVisible: Boolean,
    onMenuToggle: () -> Unit,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onArticleClick: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // 左侧菜单 - 使用快速消失动画
        AnimatedVisibility(
            visible = isMenuVisible,
            enter = expandHorizontally(
                animationSpec = tween(250),
                expandFrom = Alignment.Start
            ) + fadeIn(animationSpec = tween(250)),
            exit = shrinkHorizontally(
                animationSpec = tween(120), // 消失更快，120ms
                shrinkTowards = Alignment.Start
            ) + fadeOut(animationSpec = tween(120)),
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .background(Color.White)
        ) {
            NavigationMenu(
                categories = categories,
                selectedIndex = selectedCategoryIndex,
                onCategorySelected = onCategorySelected
            )
        }

        // 右侧内容区域 - 使用快速撑开动画
        Box(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(
                    animationSpec = tween(180) // 快速响应，比左侧消失稍快
                )
                .background(Color(0xFFF5F5F5))
        ) {
            // 菜单切换按钮
            IconButton(
                onClick = onMenuToggle,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = if (isMenuVisible) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = if (isMenuVisible) "关闭菜单" else "打开菜单",
                    tint = Color.Black
                )
            }

            // 标签网格
            if (selectedCategoryIndex < categories.size) {
                val selectedCategory = categories[selectedCategoryIndex]
                TagGrid(
                    articles = selectedCategory.articles,
                    onArticleClick = onArticleClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, top = 65.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}

/**
 * 左侧导航菜单
 */
@Composable
fun NavigationMenu(
    categories: List<NavCategory>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            val isSelected = index == selectedIndex
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = if (isSelected) Color(0xFF007DFF) else Color.White
                    )
                    .clickable { onCategorySelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name,
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 标签网格 - 流式布局，支持滚动
 */
@Composable
fun TagGrid(
    articles: List<NavArticle>,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFFFF6B9D), // 粉色
        Color(0xFF9B59B6), // 紫色
        Color(0xFF2ECC71), // 绿色
        Color(0xFF1ABC9C), // 青色
    )
    
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalSpacing = 12.dp,
            verticalSpacing = 12.dp
        ) {
            articles.forEachIndexed { index, article ->
                val colorIndex = index % colors.size
                val borderColor = colors[colorIndex]
                
                TagItem(
                    title = article.title ?: "",
                    link = article.link,
                    borderColor = borderColor,
                    onClick = {
                        article.link?.let(onArticleClick)
                    }
                )
            }
        }
    }
}

/**
 * 流式布局 - 类似FlowRow
 */
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val horizontalSpacingPx = with(density) { horizontalSpacing.roundToPx() }
        val verticalSpacingPx = with(density) { verticalSpacing.roundToPx() }
        
        val rows = mutableListOf<List<Placeable>>()
        val rowWidths = mutableListOf<Int>()
        var currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0
        
        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
            
            val itemWidth = placeable.width + if (currentRow.isNotEmpty()) horizontalSpacingPx else 0
            
            if (currentRowWidth + itemWidth <= constraints.maxWidth || currentRow.isEmpty()) {
                // 可以放在当前行
                currentRow.add(placeable)
                currentRowWidth += itemWidth
            } else {
                // 需要换行
                rows.add(currentRow)
                rowWidths.add(currentRowWidth)
                currentRow = mutableListOf(placeable)
                currentRowWidth = placeable.width
            }
        }
        
        // 添加最后一行
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            rowWidths.add(currentRowWidth)
        }
        
        // 计算总高度
        val totalHeight = if (rows.isEmpty()) {
            0
        } else {
            rows.sumOf { row -> row.maxOfOrNull { it.height } ?: 0 } + 
            (rows.size - 1) * verticalSpacingPx
        }
        
        layout(constraints.maxWidth, totalHeight) {
            var y = 0
            rows.forEach { row ->
                val rowHeight = row.maxOfOrNull { it.height } ?: 0
                var x = 0
                
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + horizontalSpacingPx
                }
                
                y += rowHeight + verticalSpacingPx
            }
        }
    }
}

/**
 * 标签项
 */
@Composable
fun TagItem(
    title: String,
    link: String?,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = link != null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

