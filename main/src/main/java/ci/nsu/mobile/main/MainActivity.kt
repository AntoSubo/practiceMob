package ci.nsu.mobile.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import ci.nsu.mobile.main.data.storage.TokenManager
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.navigation.NavGraph

class MainActivity : ComponentActivity() {
    private lateinit var serviceLocator: ServiceLocator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        TokenManager.init(this)
        TokenManager.clear()

        serviceLocator = ServiceLocator(this)

        setContent {
            MaterialTheme {
                NavGraph(serviceLocator = serviceLocator)
            }
        }
    }
}