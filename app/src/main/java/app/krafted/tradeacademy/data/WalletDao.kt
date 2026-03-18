package app.krafted.tradeacademy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWallet(): Flow<WalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWallet(wallet: WalletEntity)

    @Query("UPDATE wallet SET cashBalance = :balance WHERE id = 1")
    suspend fun updateBalance(balance: Double)
}
