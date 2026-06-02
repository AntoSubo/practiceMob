package ci.nsu.mobile.main.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposit_calculations WHERE userId = :userId ORDER BY calculationDate DESC")
    fun getCalculationsByUserId(userId: Long): Flow<List<DepositCalculation>>

    @Insert
    suspend fun insert(calculation: DepositCalculation)

    @Query("SELECT * FROM deposit_calculations WHERE id = :id")
    suspend fun getCalculationById(id: Long): DepositCalculation?

    @Delete
    suspend fun delete(calculation: DepositCalculation)
}