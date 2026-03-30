package ci.nsu.mobile.main.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ci.nsu.mobile.main.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Результат") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Стартовый взнос: ${viewModel.initialAmount}")
                    Text("Срок: ${viewModel.periodMonths} мес.")
                    Text("Ставка: ${viewModel.interestRate}%")
                    Text("Пополнение: ${viewModel.monthlyTopUp.ifBlank { "не указано" }}/мес.")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Начисленные проценты: ${String.format("%.2f", viewModel.interestEarned)}")
                    Text("Итоговая сумма: ${String.format("%.2f", viewModel.finalAmount)}", style = MaterialTheme.typography.titleLarge)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    viewModel.saveCalculation()
                    Toast.makeText(context, "Сохранено!", Toast.LENGTH_SHORT).show()
                    viewModel.clearData()
                    navController.popBackStack("home", false)
                }) {
                    Text("Сохранить")
                }
                OutlinedButton(onClick = {
                    viewModel.clearData()
                    navController.popBackStack("home", false)
                }) {
                    Text("В начало")
                }
            }
        }
    }
}
