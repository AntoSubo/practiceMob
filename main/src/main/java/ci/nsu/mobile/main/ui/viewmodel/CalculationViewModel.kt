package ci.nsu.mobile.main.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculationViewModel : ViewModel() {

    private val _initialAmount = MutableLiveData<Double?>()
    val initialAmount: LiveData<Double?> = _initialAmount

    private val _periodMonths = MutableLiveData<Int?>()
    val periodMonths: LiveData<Int?> = _periodMonths

    private val _interestRate = MutableLiveData<Double?>()
    val interestRate: LiveData<Double?> = _interestRate

    private val _monthlyTopUp = MutableLiveData<Double?>()
    val monthlyTopUp: LiveData<Double?> = _monthlyTopUp

    fun setInitialAmount(value: Double?) { _initialAmount.value = value }
    fun setPeriodMonths(value: Int?) { _periodMonths.value = value }
    fun setInterestRate(value: Double?) { _interestRate.value = value }
    fun setMonthlyTopUp(value: Double?) { _monthlyTopUp.value = value }

    fun calculateFinalAmount(): Pair<Double, Double>? {
        val initial = _initialAmount.value ?: return null
        val period = _periodMonths.value ?: return null
        val rate = _interestRate.value ?: return null
        val monthly = _monthlyTopUp.value ?: 0.0

        val years = period / 12.0
        val interest = initial * (rate / 100) * years
        val totalTopUp = monthly * period
        val finalAmount = initial + interest + totalTopUp

        return Pair(finalAmount, interest)
    }

    fun clear() {
        _initialAmount.value = null
        _periodMonths.value = null
        _interestRate.value = null
        _monthlyTopUp.value = null
    }
}