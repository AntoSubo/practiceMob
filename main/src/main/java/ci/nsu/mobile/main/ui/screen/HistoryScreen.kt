package ci.nsu.mobile.main.ui.screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: MainViewModel) {
    val historyList by viewModel.history.observeAsState(emptyList())
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
                                Text("Пополнение: ${item.monthlyTopUp}/мес.")
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