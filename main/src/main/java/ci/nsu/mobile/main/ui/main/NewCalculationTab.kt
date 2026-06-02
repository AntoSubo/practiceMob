package ci.nsu.mobile.main.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewCalculationViewModel(private val serviceLocator: ServiceLocator) : ViewModel() {
    private val _initialAmount = MutableStateFlow("")
    val initialAmount: StateFlow<String> = _initialAmount
    fun updateInitialAmount(v: String) { _initialAmount.value = v }

    private val _periodMonths = MutableStateFlow("")
    val periodMonths: StateFlow<String> = _periodMonths
    fun updatePeriodMonths(v: String) { _periodMonths.value = v }

    private val _monthlyTopUp = MutableStateFlow("")
    val monthlyTopUp: StateFlow<String> = _monthlyTopUp
    fun updateMonthlyTopUp(v: String) { _monthlyTopUp.value = v }

    private val _result = MutableStateFlow<DepositCalculation?>(null)
    val result: StateFlow<DepositCalculation?> = _result

    private val _availableRates = MutableStateFlow<List<Double>>(emptyList())
    val availableRates: StateFlow<List<Double>> = _availableRates

    private val _selectedRate = MutableStateFlow<Double?>(null)
    val selectedRate: StateFlow<Double?> = _selectedRate

    private val _periodError = MutableStateFlow<String?>(null)
    val periodError: StateFlow<String?> = _periodError

    fun updatePeriodMonthsAndRecalculate(v: String) {
        _periodMonths.value = v
        val months = v.toIntOrNull() ?: 0
        if (months <= 0) {
            _availableRates.value = emptyList()
            _selectedRate.value = null
            _periodError.value = "Укажите срок вклада"
        } else {
            _periodError.value = null
            _availableRates.value = when {
                months < 6 -> listOf(15.0)
                months < 12 -> listOf(10.0)
                else -> listOf(5.0)
            }
            _selectedRate.value = _availableRates.value.firstOrNull()
        }
    }

    fun selectRate(rate: Double) {
        _selectedRate.value = rate
    }

    fun calculate() {
        val p = _initialAmount.value.toDoubleOrNull() ?: 0.0
        val n = _periodMonths.value.toIntOrNull() ?: 0
        val r = (_selectedRate.value ?: 0.0) / 100.0 / 12.0
        val m = _monthlyTopUp.value.toDoubleOrNull()
        var total = p
        var earned = 0.0
        for (i in 1..n) {
            val interest = total * r
            earned += interest
            total += interest
            if (m != null && m > 0) total += m
        }
        _result.value = DepositCalculation(
            userId = 1L,
            initialAmount = p,
            periodMonths = n,
            interestRate = _selectedRate.value ?: 0.0,
            monthlyTopUp = m,
            finalAmount = total,
            interestEarned = earned,
            calculationDate = System.currentTimeMillis()
        )

    }

    fun save() {
        viewModelScope.launch {
            _result.value?.let { serviceLocator.depositRepository.saveCalculation(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCalculationTab(serviceLocator: ServiceLocator) {
    val viewModel: NewCalculationViewModel = viewModel(factory = serviceLocator.viewModelFactory)
    val context = LocalContext.current

    val initialAmount by viewModel.initialAmount.collectAsState()
    val periodMonths by viewModel.periodMonths.collectAsState()
    val monthlyTopUp by viewModel.monthlyTopUp.collectAsState()
    val result by viewModel.result.collectAsState()
    val availableRates by viewModel.availableRates.collectAsState()
    val selectedRate by viewModel.selectedRate.collectAsState()
    val periodError by viewModel.periodError.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Новый расчёт", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = initialAmount, onValueChange = viewModel::updateInitialAmount,
            label = { Text("Сумма (руб.)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = periodMonths, onValueChange = viewModel::updatePeriodMonthsAndRecalculate,
            label = { Text("Срок (мес.)") },
            isError = periodError != null,
            supportingText = { periodError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Выпадающий список ставок
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedRate?.let { "$it%" } ?: "Выберите ставку",
                onValueChange = {},
                readOnly = true,
                label = { Text("Ставка") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                isError = availableRates.isEmpty() && periodError == null
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableRates.forEach { rate ->
                    DropdownMenuItem(
                        text = { Text("$rate%") },
                        onClick = {
                            viewModel.selectRate(rate)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = monthlyTopUp, onValueChange = viewModel::updateMonthlyTopUp,
            label = { Text("Ежемес. пополнение") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.calculate() },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedRate != null && initialAmount.isNotEmpty() && periodMonths.isNotEmpty()
        ) {
            Text("Рассчитать")
        }

        result?.let { r ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Итог: ${String.format("%.2f", r.finalAmount)} руб.")
                    Text("Доход: ${String.format("%.2f", r.interestEarned)} руб.",
                        color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.save()
                    Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}