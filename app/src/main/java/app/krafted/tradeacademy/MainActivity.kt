package app.krafted.tradeacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.krafted.tradeacademy.ui.HomeScreen
import app.krafted.tradeacademy.ui.MarketScreen
import app.krafted.tradeacademy.ui.NewsTipsScreen
import app.krafted.tradeacademy.ui.PortfolioScreen
import app.krafted.tradeacademy.ui.SplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.krafted.tradeacademy.ui.theme.TradeAcademyTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.tradeacademy.viewmodel.MarketViewModel

sealed class Screen(val route: String, val label: String) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home")
    object Market : Screen("market", "Market")
    object NewsTips : Screen("news_tips", "News & Tips")
    object Portfolio : Screen("portfolio", "Portfolio")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeAcademyTheme {
                TradeAcademyApp()
            }
        }
    }
}

@Composable
fun TradeAcademyApp() {
    val navController = rememberNavController()
    val marketViewModel: MarketViewModel = viewModel()
    val screens = listOf(Screen.Home, Screen.Market, Screen.NewsTips, Screen.Portfolio)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF090C14),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            if (currentDestination?.route != Screen.Splash.route) {
                NavigationBar {
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                when (screen) {
                                    Screen.Home -> Icon(Icons.Filled.Home, contentDescription = "Home")
                                    Screen.Market -> Icon(Icons.Filled.List, contentDescription = "Market")
                                    Screen.NewsTips -> Icon(Icons.Filled.Info, contentDescription = "News & Tips")
                                    Screen.Portfolio -> Icon(Icons.Filled.Person, contentDescription = "Portfolio")
                                    Screen.Splash -> {}
                                }
                            },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally(tween(280)) { it / 4 } },
            exitTransition = { slideOutHorizontally(tween(280)) { -it / 4 } },
            popEnterTransition = { slideInHorizontally(tween(280)) { -it / 4 } },
            popExitTransition = { slideOutHorizontally(tween(280)) { it / 4 } }
        ) {
            composable(Screen.Splash.route) { SplashScreen(navController) }
            composable(Screen.Home.route) { HomeScreen(navController, marketViewModel = marketViewModel) }
            composable(
                route = Screen.Market.route,
                exitTransition = { ExitTransition.None },
                popExitTransition = { ExitTransition.None }
            ) { MarketScreen(marketViewModel = marketViewModel) }
            composable(Screen.NewsTips.route) { NewsTipsScreen() }
            composable(Screen.Portfolio.route) { PortfolioScreen(marketViewModel = marketViewModel) }
        }
    }
}
