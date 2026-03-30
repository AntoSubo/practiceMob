package ci.nsu.mobile.main.ui.screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTwoScreen(navController: NavController, viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }

    val currentRate = viewModel.interestRate.toString()

    Scaffold(topBar = { TopAppBar(title = { Text("Шаг 2: Доп. параметры") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = "$currentRate%",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Доступная процентная ставка") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("$currentRate%") },
                        onClick = { expanded = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.monthlyTopUp,
                onValueChange = { viewModel.monthlyTopUp = it },
                label = { Text("Ежемесячное пополнение (необязательно)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Назад")
                }
                Button(onClick = {
                    viewModel.monthlyTopUp = viewModel.monthlyTopUp.ifBlank { "0" }
                    viewModel.calculateResult()
                    navController.navigate("result")
                }) {
                    Text("Рассчитать")
                }
            }
        }
    }
}