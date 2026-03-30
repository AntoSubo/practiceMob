package ci.nsu.mobile.main.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ci.nsu.mobile.main.ui.screen.HistoryScreen
import ci.nsu.mobile.main.ui.screen.MainScreen
import ci.nsu.mobile.main.ui.screen.ResultScreen
import ci.nsu.mobile.main.ui.screen.StepOneScreen
import ci.nsu.mobile.main.ui.screen.StepTwoScreen

@Composable
fun DepositApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { MainScreen(navController) }
        composable("step_one") { StepOneScreen(navController, viewModel) }
        composable("step_two") { StepTwoScreen(navController, viewModel) }
        composable("result") { ResultScreen(navController, viewModel) }
        composable("history") { HistoryScreen(navController, viewModel) }
    }
}

@Composable
fun AppNavHost() = DepositApp()
