package ci.nsu.mobile.main.ui.screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOneScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Шаг 1: Основные параметры") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = viewModel.initialAmount,
                onValueChange = { viewModel.initialAmount = it },
                label = { Text("Стартовый взнос") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.periodMonths,
                onValueChange = { viewModel.periodMonths = it },
                label = { Text("Срок вклада (в месяцах)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = { navController.popBackStack("home", false) }) {
                    Text("В начало")
                }
                Button(onClick = {
                    if (viewModel.initialAmount.isBlank() || viewModel.periodMonths.isBlank()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.interestRate = viewModel.determineInterestRate()
                        navController.navigate("step_two")
                    }
                }) {
                    Text("Далее")
                }
            }
        }
    }
}