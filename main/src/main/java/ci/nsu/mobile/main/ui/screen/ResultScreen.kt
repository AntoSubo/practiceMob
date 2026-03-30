package ci.nsu.mobile.main.ui.screen
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
                    Text("Пополнение: ${viewModel.monthlyTopUp.ifBlank { "0" }}/мес.")
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