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

data class RateOption(
    val rate: Double,
    val minMonths: Int,
    val maxMonths: Int,
    val label: String
)

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

    val availableRates: List<RateOption> = listOf(
        RateOption(5.0, 1, 5, "5% (1-5 месяцев)"),
        RateOption(10.0, 6, 11, "10% (6-11 месяцев)"),
        RateOption(15.0, 12, Int.MAX_VALUE, "15% (от 12 месяцев)")
    )

    private val _selectedRate = MutableStateFlow<RateOption?>(null)
    val selectedRate: StateFlow<RateOption?> = _selectedRate

    fun selectRate(option: RateOption) {
        _selectedRate.value = option
        val currentMonths = _periodMonths.value.toIntOrNull() ?: 0
        if (currentMonths !in option.minMonths..option.maxMonths) {
            val closest = if (currentMonths < option.minMonths) option.minMonths else option.maxMonths
            _periodMonths.value = closest.toString()
        }
    }

    fun calculate() {
        val p = _initialAmount.value.toDoubleOrNull() ?: 0.0
        val n = _periodMonths.value.toIntOrNull() ?: 0
        val r = (_selectedRate.value?.rate ?: 0.0) / 100.0 / 12.0
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
            interestRate = _selectedRate.value?.rate ?: 0.0,
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
    val availableRates = viewModel.availableRates
    val selectedRate by viewModel.selectedRate.collectAsState()
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
            value = periodMonths, onValueChange = viewModel::updatePeriodMonths,
            label = { Text("Срок (мес.)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedRate?.label ?: "Выберите ставку",
                onValueChange = {},
                readOnly = true,
                label = { Text("Ставка (автоподстановка срока)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableRates.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            viewModel.selectRate(option)
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