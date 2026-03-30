package ci.nsu.mobile.main.data

import androidx.lifecycle.LiveData

class DepositRepository(private val depositDao: DepositDao) {
    val allHistory: LiveData<List<DepositCalculation>> = depositDao.allHistory

    fun getAllHistory(): LiveData<List<DepositCalculation>> = allHistory

    suspend fun insert(calculation: DepositCalculation) {
        depositDao.insert(calculation)
    }
}