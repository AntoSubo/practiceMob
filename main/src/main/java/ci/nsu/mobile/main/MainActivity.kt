package ci.nsu.mobile.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import ci.nsu.mobile.main.data.network.NetworkModule
import ci.nsu.mobile.main.data.storage.TokenManager
import ci.nsu.mobile.main.domain.AuthRepository
import ci.nsu.mobile.main.ui.theme.PracticeTheme   // <-- Убедись, что у тебя такая тема

class MainActivity : ComponentActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(context = this)
        tokenManager.clearToken()
        val apiService = NetworkModule.provideApiService(tokenManager)
        val publicApiService = NetworkModule.providePublicApiService()
        authRepository = AuthRepository(apiService, publicApiService, tokenManager)

        setContent {
            PracticeTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    authRepository = authRepository,
                    tokenManager = tokenManager
                )
            }
        }
    }
}