package ci.nsu.mobile.main.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import ci.nsu.mobile.main.data.DepositCalculation
import ci.nsu.mobile.main.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: MainViewModel) {
    val historyState = remember { mutableStateOf(emptyList<DepositCalculation>()) }
    DisposableEffect(viewModel.history) {
        val observer = Observer<List<DepositCalculation>> { historyState.value = it ?: emptyList() }
        viewModel.history.observeForever(observer)
        onDispose { viewModel.history.removeObserver(observer) }
    }
    val historyList = historyState.value
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История расчётов") },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(historyList) { item ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { isExpanded = !isExpanded }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Дата: ${dateFormat.format(Date(item.calculationDate))}", style = MaterialTheme.typography.labelSmall)
                            Text("Стартовый взнос: ${item.initialAmount}")
                            Text("Итог: ${String.format(Locale.US, "%.2f", item.finalAmount)}", style = MaterialTheme.typography.titleMedium)

                            if (isExpanded) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("Срок вклада: ${item.periodMonths} мес.")
                                Text("Ставка: ${item.interestRate}%")
                                Text("Пополнение: ${item.monthlyTopUp?.toString() ?: "не указано"}/мес.")
                                Text("Начисленные проценты: ${String.format(Locale.US, "%.2f", item.interestEarned)}")
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.popBackStack("home", inclusive = false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Вернуться на главную")
            }
        }
    }
}
