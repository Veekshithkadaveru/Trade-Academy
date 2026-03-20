package app.krafted.tradeacademy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import app.krafted.tradeacademy.data.AppDatabase
import app.krafted.tradeacademy.data.HoldingEntity
import app.krafted.tradeacademy.data.INITIAL_BALANCE
import app.krafted.tradeacademy.data.TradeEntity
import app.krafted.tradeacademy.data.TradeType
import app.krafted.tradeacademy.data.WalletEntity
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuySellUiState(
    val cashBalance: Double = 0.0,
    val currentHolding: Double = 0.0,
    val isLoading: Boolean = false,
    val tradeResult: TradeResult? = null
)

sealed class TradeResult {
    object Success : TradeResult()
    data class Error(val message: String) : TradeResult()
}

class BuySellViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val walletDao = db.walletDao()
    private val holdingDao = db.holdingDao()
    private val tradeDao = db.tradeDao()

    private val _uiState = MutableStateFlow(BuySellUiState())
    val uiState: StateFlow<BuySellUiState> = _uiState.asStateFlow()

    private fun roundMoney(value: Double): Double = Math.round(value * 100.0) / 100.0
    private fun roundPrice(value: Double): Double = Math.round(value * 10000.0) / 10000.0

    fun loadAssetContext(assetId: String) {
        viewModelScope.launch {
            val wallet = walletDao.getWallet().first()
            val holding = holdingDao.getHolding(assetId)
            _uiState.update {
                it.copy(
                    cashBalance = wallet?.cashBalance ?: INITIAL_BALANCE,
                    currentHolding = holding?.quantity ?: 0.0
                )
            }
        }
    }

    fun buy(assetId: String, price: Double, quantity: Double) {
        viewModelScope.launch {
            if (quantity <= 0 || price <= 0) {
                _uiState.update { it.copy(tradeResult = TradeResult.Error("Enter a valid quantity")) }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true) }

            try {
                var newBalance = 0.0
                var newQty = 0.0
                db.withTransaction {
                    val wallet = walletDao.getWalletOnce() ?: WalletEntity()
                    val totalCost = price * quantity
                    if (totalCost > wallet.cashBalance) {
                        throw IllegalStateException("Insufficient funds")
                    }

                    val existing = holdingDao.getHolding(assetId)
                    newQty = (existing?.quantity ?: 0.0) + quantity
                    val newAvg = if (existing != null) {
                        roundPrice(((existing.avgBuyPrice * existing.quantity) + (price * quantity)) / newQty)
                    } else {
                        price
                    }

                    newBalance = roundMoney(wallet.cashBalance - totalCost)
                    holdingDao.upsertHolding(HoldingEntity(assetId, newQty, newAvg))
                    walletDao.updateBalanceAndSeedIfMissing(newBalance)
                    tradeDao.insertTrade(TradeEntity(assetId = assetId, type = TradeType.BUY, quantity = quantity, price = price))
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cashBalance = newBalance,
                        currentHolding = newQty,
                        tradeResult = TradeResult.Success
                    )
                }
            } catch (e: IllegalStateException) {
                _uiState.update { it.copy(isLoading = false, tradeResult = TradeResult.Error(e.message ?: "Trade failed")) }
            } catch (e: Exception) {
                Log.e("BuySellViewModel", "Unexpected error during buy", e)
                _uiState.update { it.copy(isLoading = false, tradeResult = TradeResult.Error("Trade failed unexpectedly")) }
            }
        }
    }

    fun sell(assetId: String, price: Double, quantity: Double) {
        viewModelScope.launch {
            if (quantity <= 0 || price <= 0) {
                _uiState.update { it.copy(tradeResult = TradeResult.Error("Enter a valid quantity")) }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true) }

            try {
                var newBalance = 0.0
                var newQty = 0.0
                db.withTransaction {
                    val existing = holdingDao.getHolding(assetId)
                    val wallet = walletDao.getWalletOnce() ?: WalletEntity()

                    if (existing == null || quantity > existing.quantity) {
                        throw IllegalStateException("Insufficient holdings")
                    }

                    newQty = existing.quantity - quantity
                    if (newQty < 0.0001) {
                        holdingDao.deleteHolding(assetId)
                    } else {
                        holdingDao.upsertHolding(existing.copy(quantity = newQty))
                    }

                    val proceeds = price * quantity
                    newBalance = roundMoney(wallet.cashBalance + proceeds)
                    walletDao.updateBalanceAndSeedIfMissing(newBalance)
                    tradeDao.insertTrade(TradeEntity(assetId = assetId, type = TradeType.SELL, quantity = quantity, price = price))
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cashBalance = newBalance,
                        currentHolding = if (newQty < 0.0001) 0.0 else newQty,
                        tradeResult = TradeResult.Success
                    )
                }
            } catch (e: IllegalStateException) {
                _uiState.update { it.copy(isLoading = false, tradeResult = TradeResult.Error(e.message ?: "Trade failed")) }
            } catch (e: Exception) {
                Log.e("BuySellViewModel", "Unexpected error during sell", e)
                _uiState.update { it.copy(isLoading = false, tradeResult = TradeResult.Error("Trade failed unexpectedly")) }
            }
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(tradeResult = null) }
    }
}
