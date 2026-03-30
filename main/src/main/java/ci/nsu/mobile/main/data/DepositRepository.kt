package ci.nsu.mobile.depositapp.data

import kotlinx.coroutines.flow.Flow

class DepositRepository(
    private val dao: DepositDao
) {
    val allCalculations: Flow<List<DepositCalculation>> = dao.getAll()

    suspend fun insert(calculation: DepositCalculation): Long = dao.insert(calculation)

    suspend fun getById(id: Int): DepositCalculation? = dao.getById(id)
}