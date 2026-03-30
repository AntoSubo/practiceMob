package ci.nsu.mobile.main.ui.screen

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