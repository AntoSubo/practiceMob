package ci.nsu.mobile.main.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.ui.viewmodel.CalculationViewModel

@Composable
fun Step1Screen(
    onNavigateBack: () -> Unit,
    onNavigateToStep2: () -> Unit
) {
    //  ViewModel общая для всех
    val viewModel: CalculationViewModel = viewModel()

    // Состояния для полей ввода
    var initialAmountText by remember { mutableStateOf("") }
    var periodMonthsText by remember { mutableStateOf("") }

    // Ошибки валидации
    var initialAmountError by remember { mutableStateOf(false) }
    var periodMonthsError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Параметры вклада",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = initialAmountText,
            onValueChange = {
                initialAmountText = it
                initialAmountError = false
            },
            label = { Text("Стартовый взнос") },
            placeholder = { Text("Например: 10000") },
            isError = initialAmountError,
            supportingText = {
                if (initialAmountError) {
                    Text("Введите положительное число")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = periodMonthsText,
            onValueChange = {
                periodMonthsText = it
                periodMonthsError = false
            },
            label = { Text("Срок вклада (месяцы)") },
            placeholder = { Text("Например: 12") },
            isError = periodMonthsError,
            supportingText = {
                if (periodMonthsError) {
                    Text("Введите положительное целое число")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onNavigateBack) {
                Text("В начало")
            }

            Button(
                onClick = {
                    // Валидация
                    val initialAmount = initialAmountText.toDoubleOrNull()
                    val periodMonths = periodMonthsText.toIntOrNull()

                    val isInitialValid = initialAmount != null && initialAmount > 0
                    val isPeriodValid = periodMonths != null && periodMonths > 0

                    initialAmountError = !isInitialValid
                    periodMonthsError = !isPeriodValid

                    if (isInitialValid && isPeriodValid) {
                        // Сохраняем в ViewModel
                        viewModel.setInitialAmount(initialAmount)
                        viewModel.setPeriodMonths(periodMonths)
                        // Переходим к следующему шагу
                        onNavigateToStep2()
                    }
                }
            ) {
                Text("Далее")
            }
        }
    }
}