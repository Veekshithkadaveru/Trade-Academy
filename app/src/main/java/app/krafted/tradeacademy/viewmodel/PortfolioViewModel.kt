package app.krafted.tradeacademy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.tradeacademy.data.AppDatabase
import app.krafted.tradeacademy.data.HoldingEntity
import app.krafted.tradeacademy.data.INITIAL_BALANCE
import app.krafted.tradeacademy.data.TradeEntity
import app.krafted.tradeacademy.data.WalletEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.room.withTransaction
import kotlinx.coroutines.launch

class PortfolioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val walletDao = db.walletDao()
    private val holdingDao = db.holdingDao()
    private val tradeDao = db.tradeDao()

    val cashBalance: StateFlow<Double> = walletDao.getWallet()
        .map { it?.cashBalance ?: INITIAL_BALANCE }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), INITIAL_BALANCE)

    val holdings: StateFlow<List<HoldingEntity>> = holdingDao.getAllHoldings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tradeHistory: StateFlow<List<TradeEntity>> = tradeDao.getAllTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun reset() {
        viewModelScope.launch {
            db.withTransaction {
                walletDao.upsertWallet(WalletEntity(cashBalance = INITIAL_BALANCE))
                holdingDao.clearAll()
                tradeDao.clearAll()
            }
        }
    }
}
