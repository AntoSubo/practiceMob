package ci.nsu.mobile.main.data

import androidx.lifecycle.LiveData

class DepositRepository(private val depositDao: DepositDao) {
    fun getAllHistory(): LiveData<List<DepositCalculation>> = depositDao.getAllHistory()

    suspend fun insert(calculation: DepositCalculation) {
        depositDao.insert(calculation)
    }
}
