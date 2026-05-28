package ci.nsu.mobile.main.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyCalculationsViewModel(private val serviceLocator: ServiceLocator) : ViewModel() {
    private val _calculations = MutableStateFlow<List<DepositCalculation>>(emptyList())
    val calculations: StateFlow<List<DepositCalculation>> = _calculations

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            serviceLocator.depositRepository.getCalculationsByUserId(1L).collect { list ->
                _calculations.value = list
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun MyCalculationsTab(serviceLocator: ServiceLocator, onLogout: () -> Unit) {
    val viewModel: MyCalculationsViewModel = viewModel(factory = serviceLocator.viewModelFactory)
    val calculations by viewModel.calculations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Мои расчёты", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onLogout) { Text("Выйти") }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (calculations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет сохранённых расчётов")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(calculations) { calc ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Сумма: ${String.format("%.2f", calc.initialAmount)} руб.")
                            Text("Срок: ${calc.periodMonths} мес.")
                            Text("Ставка: ${calc.interestRate}%")
                            Text("Итог: ${String.format("%.2f", calc.finalAmount)} руб.")
                            Text("Доход: ${String.format("%.2f", calc.interestEarned)} руб.")
                            Text(dateFormat.format(Date(calc.calculationDate)),
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}