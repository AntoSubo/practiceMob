package ci.nsu.mobile.main.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ci.nsu.mobile.main.di.ServiceLocator

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    serviceLocator: ServiceLocator
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("👥") }, label = { Text("Пользователи") },
                    selected = selectedTab == 0, onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Text("💰") }, label = { Text("Мои расчёты") },
                    selected = selectedTab == 1, onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Text("📊") }, label = { Text("Новый расчёт") },
                    selected = selectedTab == 2, onClick = { selectedTab = 2 }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> UsersTab(serviceLocator)
                1 -> MyCalculationsTab(serviceLocator, onLogout)
                2 -> NewCalculationTab(serviceLocator)
            }
        }
    }
}