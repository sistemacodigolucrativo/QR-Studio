package br.com.universalbuild.kotlinmaster

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.universalbuild.feature.home.ui.HomeRoute
@Composable
fun MasterNavigation(isExpanded: Boolean) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeDestination) {
        composable<HomeDestination> {
            HomeRoute(isExpanded = isExpanded)
        }
    }
}
