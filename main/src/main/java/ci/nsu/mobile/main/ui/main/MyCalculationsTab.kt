package ci.nsu.mobile.main.ui.main

import androidx.compose.foundation.clickable
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
    private val _allCalculations = MutableStateFlow<List<DepositCalculation>>(emptyList())
    val calculations: StateFlow<List<DepositCalculation>> = _allCalculations

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedCalculation = MutableStateFlow<DepositCalculation?>(null)
    val selectedCalculation: StateFlow<DepositCalculation?> = _selectedCalculation

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText

    private val _filteredCalculations = MutableStateFlow<List<DepositCalculation>>(emptyList())
    val filteredCalculations: StateFlow<List<DepositCalculation>> = _filteredCalculations

    init {
        viewModelScope.launch {
            serviceLocator.depositRepository.getCalculationsByUserId(1L).collect { list ->
                _allCalculations.value = list
                applyFilter()
                _isLoading.value = false
            }
        }
    }

    fun updateFilter(text: String) {
        _filterText.value = text
        applyFilter()
    }

    private fun applyFilter() {
        val filter = _filterText.value.lowercase()
        _filteredCalculations.value = if (filter.isEmpty()) {
            _allCalculations.value
        } else {
            _allCalculations.value.filter {
                it.initialAmount.toString().contains(filter) ||
                        it.finalAmount.toString().contains(filter) ||
                        it.periodMonths.toString().contains(filter) ||
                        it.interestRate.toString().contains(filter)
            }
        }
    }

    fun selectCalculation(calc: DepositCalculation) {
        _selectedCalculation.value = calc
    }

    fun clearSelection() {
        _selectedCalculation.value = null
    }

    fun deleteCalculation(calc: DepositCalculation) {
        viewModelScope.launch {
            serviceLocator.depositRepository.deleteCalculation(calc)
            _selectedCalculation.value = null
        }
    }
}

@Composable
fun MyCalculationsTab(serviceLocator: ServiceLocator, onLogout: () -> Unit) {
    val viewModel: MyCalculationsViewModel = viewModel(factory = serviceLocator.viewModelFactory)
    val calculations by viewModel.filteredCalculations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCalculation by viewModel.selectedCalculation.collectAsState()
    val filterText by viewModel.filterText.collectAsState()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    if (selectedCalculation != null) {
        // Экран деталей
        val calc = selectedCalculation!!
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Детали расчёта", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Дата: ${dateFormat.format(Date(calc.calculationDate))}")
                    Text("Сумма: ${String.format("%.2f", calc.initialAmount)} руб.")
                    Text("Срок: ${calc.periodMonths} мес.")
                    Text("Ставка: ${calc.interestRate}%")
                    if (calc.monthlyTopUp != null && calc.monthlyTopUp > 0) {
                        Text("Пополнение: ${String.format("%.2f", calc.monthlyTopUp)} руб.")
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Доход: ${String.format("%.2f", calc.interestEarned)} руб.",
                        color = MaterialTheme.colorScheme.primary)
                    Text("Итог: ${String.format("%.2f", calc.finalAmount)} руб.",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.deleteCalculation(calc) }) {
                    Text("Удалить")
                }
                Button(onClick = { viewModel.clearSelection() }) {
                    Text("Назад")
                }
            }
        }
    } else {
        // Список расчётов
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Мои расчёты", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = onLogout) { Text("Выйти") }
            }

            OutlinedTextField(
                value = filterText,
                onValueChange = { viewModel.updateFilter(it) },
                label = { Text("Фильтр (сумма, срок, ставка)") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.selectCalculation(calc) }
                        ) {
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
}