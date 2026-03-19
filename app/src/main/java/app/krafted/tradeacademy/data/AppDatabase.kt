package app.krafted.tradeacademy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WalletEntity::class, HoldingEntity::class, TradeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TradeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun holdingDao(): HoldingDao
    abstract fun tradeDao(): TradeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trade_academy.db"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT OR IGNORE INTO wallet (id, cashBalance) VALUES (1, $INITIAL_BALANCE)")
                    }
                }).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
