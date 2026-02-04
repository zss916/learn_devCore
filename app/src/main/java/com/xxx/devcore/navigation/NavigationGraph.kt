package com.xxx.devcore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ssz.network.LoginStateManager
import com.ssz.network.repository.LoginRepository
import com.xxx.devcore.main.TabItem
import com.xxx.devcore.main.home.HomeScreen
import com.xxx.devcore.main.mine.FavoriteScreen
import com.xxx.devcore.main.mine.MineScreen
import com.xxx.devcore.main.mine.SettingsScreen
import com.xxx.devcore.main.mine.UserInfoScreen
import com.xxx.devcore.main.mine.MineViewModel
import com.xxx.devcore.main.mine.MineUiState
import com.xxx.devcore.main.navigation.NavigationScreen
import com.xxx.devcore.main.square.SquareScreen
import com.xxx.devcore.main.webview.WebPageScreen
import kotlinx.coroutines.launch

/**
 * 导航图配置，新增页面都在这里添加
 */
@Composable
fun AppNavigationGraph(
    navController: NavHostController,
    startDestination: String = TabItem.HOME.route
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 首页
        composable(TabItem.HOME.route) {
            HomeScreen(
                onArticleClick = { link ->
                    navController.navigateToWebView(link)
                }
            )
        }
        
        // 广场
        composable(TabItem.SQUARE.route) {
            SquareScreen(
                onArticleClick = { link ->
                    navController.navigateToWebView(link)
                }
            )
        }
        
        // 导航
        composable(TabItem.NAVIGATION.route) {
            NavigationScreen(navController = navController)
        }
        
        // 我的
        composable(TabItem.MINE.route) {
            MineScreen(
                onSettingsClick = { username ->
                    navController.navigateToSettings(username)
                },
                onFavoriteClick = {
                    navController.navigateToFavorite()
                }
            )
        }
        
        // 设置页面
        composable(
            route = NavigationRoutes.SETTINGS_WITH_USERNAME,
            arguments = NavigationRoutes.settingsArguments()
        ) { backStackEntry ->
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val loginRepository = LoginRepository()
            var isLoading by remember { mutableStateOf(false) }
            
            // 从MineViewModel获取用户数据
            val mineViewModel: MineViewModel = viewModel()
            val mineUiState by mineViewModel.uiState.collectAsState()
            val userCoinData = when (val state = mineUiState) {
                is MineUiState.Success -> state.userCoinData
                else -> null
            }
            
            SettingsScreen(
                onBack = { navController.popBackStack() },
                username = backStackEntry.getUsername(),
                onUserInfoClick = {
                    userCoinData?.let {
                        navController.navigateToUserInfo(it)
                    }
                },
                onLogoutClick = {
                    // 调用退出接口并清除登录状态
                    coroutineScope.launch {
                        isLoading = true
                        loginRepository.logout().collect { result ->
                            when (result) {
                                is com.ssz.network.Result.Success -> {
                                    // 退出成功，清除登录状态和 Cookie
                                    LoginStateManager.clearLoginState(context)
                                    isLoading = false
                                    // 返回上一页（会触发登录状态监听，自动跳转到登录页）
                                    navController.popBackStack()
                                }
                                is com.ssz.network.Result.Failure -> {
                                    // 即使接口调用失败，也清除本地登录状态
                                    LoginStateManager.clearLoginState(context)
                                    isLoading = false
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                },
                isLoading = isLoading
            )
        }
        
        // 用户信息页面
        composable(
            route = NavigationRoutes.USER_INFO_WITH_DATA,
            arguments = NavigationRoutes.userInfoArguments()
        ) { backStackEntry ->
            UserInfoScreen(
                onBack = { navController.popBackStack() },
                userData = backStackEntry.getUserData()
            )
        }
        
        // 收藏页面
        composable(NavigationRoutes.FAVORITE) {
            FavoriteScreen(
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }
        
        // WebView 页面
        composable(
            route = NavigationRoutes.WEBVIEW_WITH_URL,
            arguments = NavigationRoutes.webviewArguments()
        ) { backStackEntry ->
            WebPageScreen(
                url = backStackEntry.getWebViewUrl(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}

