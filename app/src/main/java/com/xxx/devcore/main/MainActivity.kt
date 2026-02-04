package com.xxx.devcore.main

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssz.login.LoginRoute
import com.xxx.devcore.ui.theme.DevCoreTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ssz.network.LoginStateManager
import com.ssz.network.RetrofitInstance
import com.xxx.devcore.navigation.AppNavigationGraph
import com.xxx.devcore.R
import androidx.compose.ui.platform.LocalResources

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化CookieJar用于持久化存储登录cookie
        RetrofitInstance.initCookieJar(applicationContext)
        
        // 设置全屏模式
        setupFullScreen()
        
        enableEdgeToEdge()
        setContent {
            DevCoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppEntry()
                }
            }
        }
    }
    
    /**
     * 设置全屏模式
     */
    private fun setupFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            // 隐藏状态栏和导航栏
            hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            // 设置沉浸式模式
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

/**
 * 应用入口，包含启动页面
 */
@Composable
fun AppEntry() {
    val context = LocalContext.current
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    
    // 监听登录状态变化
    val loginStateChanged by LoginStateManager.loginStateChanged.collectAsState()

    // 启动页面
    if (showSplash) {
        SplashScreen(
            onFinish = {
                // 检查登录状态
                isLoggedIn = LoginStateManager.isLoggedIn(context)
                showSplash = false
            }
        )
    } else {
        // 当登录状态变化时，更新 isLoggedIn
        LaunchedEffect(loginStateChanged) {
            loginStateChanged?.let { loggedIn ->
                // 如果收到 false（未登录），清除登录状态和 Cookie
                if (!loggedIn && isLoggedIn) {
                    // 清除登录状态和 Cookie
                    LoginStateManager.clearLoginState(context)
                }
                isLoggedIn = loggedIn
            }
        }
        
        // 根据登录状态跳转
        if (isLoggedIn) {
            MainScreen()
        } else {
            LoginRoute(
                onLoginSuccess = {
                    isLoggedIn = true
                    LoginStateManager.setLoggedIn(context, true)
                }
            )
        }
    }
}

/**
 * 启动页面 - 全屏显示
 */
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        // 显示启动页面至少1.5秒
        delay(1500)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 显示应用Logo
            Icon(
                painter = painterResource(id = R.mipmap.ic_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = LocalResources.current.getString(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/**
 * 底部导航栏页面枚举
 */
enum class TabItem(
    val title: String,
    val iconDefault: Int,
    val iconSelected: Int,
    val route: String
) {
    HOME("首页", R.drawable.ic_tab_home_default, R.drawable.ic_tab_home_selected, "home"),
    SQUARE("广场", R.drawable.ic_tab_square_default, R.drawable.ic_tab_square_selected, "square"),
    NAVIGATION("导航", R.drawable.ic_tab_navi_default, R.drawable.ic_tab_navi_selected, "navigation"),
    MINE("我的", R.drawable.ic_tab_mine_default, R.drawable.ic_tab_mine_selected, "mine")
}

/**
 * 主界面，包含底部导航栏
 */
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: TabItem.HOME.route

    Scaffold(
        bottomBar = {
            if (currentRoute == TabItem.HOME.route
                || currentRoute == TabItem.SQUARE.route
                || currentRoute == TabItem.NAVIGATION.route
                || currentRoute == TabItem.MINE.route) {
                NavigationBar {
                    TabItem.entries.forEach { tab ->
                        val isSelected = currentRoute == tab.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = if (isSelected) tab.iconSelected else tab.iconDefault
                                    ),
                                    contentDescription = tab.title,
                                    tint = Color.Unspecified // 使用Vector Drawable中定义的颜色
                                )
                            },
                            label = {}, // 不显示文字标签
                            alwaysShowLabel = false,
                            selected = isSelected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true //保存被弹出页面的状态（ViewModel SavedStateHandle）
                                    }
                                    launchSingleTop = true //如果目标页面在栈顶 避免重新创建实例
                                    restoreState = true //恢复目标页面之前的状态
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            ),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppNavigationGraph(navController = navController)
        }
    }
}



/**
 * loading 加载框
 * */
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

