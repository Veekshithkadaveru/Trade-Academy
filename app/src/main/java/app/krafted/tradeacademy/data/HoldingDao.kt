package app.krafted.tradeacademy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {
    @Query("SELECT * FROM holdings")
    fun getAllHoldings(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings WHERE assetId = :assetId")
    suspend fun getHolding(assetId: String): HoldingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHolding(holding: HoldingEntity)

    @Query("DELETE FROM holdings WHERE assetId = :assetId")
    suspend fun deleteHolding(assetId: String)

    @Query("DELETE FROM holdings")
    suspend fun clearAll()
}
