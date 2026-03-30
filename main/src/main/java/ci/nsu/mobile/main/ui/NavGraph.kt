package ci.nsu.mobile.main.ui

import android.app.Activity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ci.nsu.mobile.main.ui.screen.MainScreen
import ci.nsu.mobile.main.ui.screen.Step1Screen
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                onNavigateToStep1 = { navController.navigate("step1") },
                onNavigateToHistory = { navController.navigate("history") },
                onCloseApp = { (context as? Activity)?.finishAffinity() }
            )
        }

        composable("step1") {
            Step1Screen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStep2 = { navController.navigate("step2") }
            )
        }

        composable("history") {
            // TODO: HistoryScreen
            Text("Экран истории")
        }
    }
}