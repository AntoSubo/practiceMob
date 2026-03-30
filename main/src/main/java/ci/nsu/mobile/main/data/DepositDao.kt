package ci.nsu.mobile.main.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: DepositCalculation): Long

    @Query("SELECT * FROM deposit_calculations ORDER BY calculationDate DESC")
    fun getAll(): Flow<List<DepositCalculation>>

    @Query("SELECT * FROM deposit_calculations WHERE id = :id")
    suspend fun getById(id: Int): DepositCalculation?

    @Query("SELECT * FROM deposit_calculations ORDER BY calculationDate DESC")
    fun getAllHistory(): LiveData<List<DepositCalculation>>
}
