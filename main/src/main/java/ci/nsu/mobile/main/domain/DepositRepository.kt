package ci.nsu.mobile.main.domain

import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.data.local.DepositDao
import kotlinx.coroutines.flow.Flow

class DepositRepository(private val dao: DepositDao) {

    fun getCalculationsByUserId(userId: Long): Flow<List<DepositCalculation>> =
        dao.getCalculationsByUserId(userId)

    suspend fun saveCalculation(calculation: DepositCalculation) {
        dao.insert(calculation)
    }

    suspend fun getCalculationById(id: Long): DepositCalculation? =
        dao.getCalculationById(id)

    suspend fun deleteCalculation(calculation: DepositCalculation) {
        dao.delete(calculation)
    }
}