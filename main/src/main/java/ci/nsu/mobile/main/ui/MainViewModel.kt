package ci.nsu.mobile.main.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.AppDatabase
import ci.nsu.mobile.main.data.DepositCalculation
import ci.nsu.mobile.main.data.DepositRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DepositRepository
    val history: LiveData<List<DepositCalculation>>

    init {
        val depositDao = AppDatabase.getDatabase(application).depositDao()
        repository = DepositRepository(depositDao)
        history = repository.allHistory
    }

    var initialAmount by mutableStateOf("")
    var periodMonths by mutableStateOf("")
    var interestRate by mutableDoubleStateOf(0.0)
    var monthlyTopUp by mutableStateOf("")

    var finalAmount by mutableDoubleStateOf(0.0)
    var interestEarned by mutableDoubleStateOf(0.0)

    fun determineInterestRate(months: Int): Double {
        return when {
            months < 6 -> 15.0
            months in 6..11 -> 10.0
            months >= 12 -> 5.0
            else -> 0.0
        }
    }

    fun validateStepOne(): String? {
        val amount = initialAmount.toDoubleOrNull()
        val months = periodMonths.toIntOrNull()

        return when {
            initialAmount.isBlank() || periodMonths.isBlank() -> "Заполните все поля"
            amount == null -> "Введите корректный стартовый взнос"
            amount <= 0.0 -> "Стартовый взнос должен быть больше 0"
            months == null -> "Введите срок вклада целым числом"
            months <= 0 -> "Срок вклада должен быть больше 0"
            else -> null
        }
    }

    fun validateStepTwo(): String? {
        if (monthlyTopUp.isBlank()) {
            monthlyTopUp = "0"
            return null
        }

        val topUp = monthlyTopUp.toDoubleOrNull()
        return when {
            topUp == null -> "Введите корректную сумму пополнения"
            topUp < 0.0 -> "Пополнение не может быть отрицательным"
            else -> null
        }
    }

    fun calculateResult() {
        val amount = initialAmount.toDoubleOrNull() ?: 0.0
        val months = periodMonths.toIntOrNull() ?: 0
        val rate = determineInterestRate(months)
        val topUp = monthlyTopUp.toDoubleOrNull() ?: 0.0

        var total = amount
        var earned = 0.0
        val monthlyRate = rate / 100 / 12

        for (i in 1..months) {
            total += topUp
            val currentMonthInterest = total * monthlyRate
            earned += currentMonthInterest
            total += currentMonthInterest
        }

        finalAmount = total
        interestEarned = earned
        interestRate = rate
    }

    fun saveCalculation() {
        val calc = DepositCalculation(
            initialAmount = initialAmount.toDoubleOrNull() ?: 0.0,
            periodMonths = periodMonths.toIntOrNull() ?: 0,
            interestRate = interestRate,
            monthlyTopUp = monthlyTopUp.toDoubleOrNull() ?: 0.0,
            finalAmount = finalAmount,
            interestEarned = interestEarned
        )
        viewModelScope.launch {
            repository.insert(calc)
        }
    }

    fun clearData() {
        initialAmount = ""
        periodMonths = ""
        interestRate = 0.0
        monthlyTopUp = ""
        finalAmount = 0.0
        interestEarned = 0.0
    }
}
