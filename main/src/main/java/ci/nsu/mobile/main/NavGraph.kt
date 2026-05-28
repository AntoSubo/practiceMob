package ci.nsu.mobile.main.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.ui.auth.LoginScreen
import ci.nsu.mobile.main.ui.auth.LoginViewModel
import ci.nsu.mobile.main.ui.auth.RegisterScreen
import ci.nsu.mobile.main.ui.auth.RegisterViewModel
import ci.nsu.mobile.main.ui.main.MainScreen

@Composable
fun NavGraph(serviceLocator: ServiceLocator) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val viewModel: LoginViewModel = viewModel(factory = serviceLocator.viewModelFactory)
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            val viewModel: RegisterViewModel = viewModel(factory = serviceLocator.viewModelFactory)
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable("main") {
            MainScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                serviceLocator = serviceLocator
            )
        }
    }
}