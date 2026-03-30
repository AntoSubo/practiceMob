package ci.nsu.mobile.main.ui.screen

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current as Activity

    Scaffold(
        topBar = { TopAppBar(title = { Text("Расчёт вкладов") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate("step_one") }, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("Рассчитать")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("history") }, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("История расчётов")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { context.finish() }, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("Закрыть приложение")
            }
        }
    }
}
