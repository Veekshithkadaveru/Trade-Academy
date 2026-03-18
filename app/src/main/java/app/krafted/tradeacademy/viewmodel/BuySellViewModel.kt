package app.krafted.tradeacademy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.tradeacademy.data.AppDatabase
import app.krafted.tradeacademy.data.HoldingEntity
import app.krafted.tradeacademy.data.TradeEntity
import app.krafted.tradeacademy.data.TradeType
import app.krafted.tradeacademy.data.WalletEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuySellUiState(
    val cashBalance: Double = 0.0,
    val currentHolding: Double = 0.0,   // quantity held for selected asset
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

    fun loadAssetContext(assetId: String) {
        viewModelScope.launch {
            val wallet = walletDao.getWallet().first()
            val holding = holdingDao.getHolding(assetId)
            _uiState.update {
                it.copy(
                    cashBalance = wallet?.cashBalance ?: 10000.0,
                    currentHolding = holding?.quantity ?: 0.0
                )
            }
        }
    }

    fun buy(assetId: String, price: Double, quantity: Double) {
        viewModelScope.launch {
            val totalCost = price * quantity
            val wallet = walletDao.getWalletOnce() ?: WalletEntity()

            if (quantity <= 0) {
                _uiState.update { it.copy(tradeResult = TradeResult.Error("Enter a valid quantity")) }
                return@launch
            }
            if (totalCost > wallet.cashBalance) {
                _uiState.update { it.copy(tradeResult = TradeResult.Error("Insufficient funds")) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            // Weighted average buy price
            val existing = holdingDao.getHolding(assetId)
            val newQty = (existing?.quantity ?: 0.0) + quantity
            val newAvg = if (existing != null) {
                ((existing.avgBuyPrice * existing.quantity) + (price * quantity)) / newQty
            } else {
                price
            }

            holdingDao.upsertHolding(HoldingEntity(assetId, newQty, newAvg))
            walletDao.updateBalanceAndSeedIfMissing(wallet.cashBalance - totalCost)
            tradeDao.insertTrade(TradeEntity(assetId = assetId, type = TradeType.BUY, quantity = quantity, price = price))

            _uiState.update {
                it.copy(
                    isLoading = false,
                    cashBalance = wallet.cashBalance - totalCost,
                    currentHolding = newQty,
                    tradeResult = TradeResult.Success
                )
            }
        }
    }

    fun sell(assetId: String, price: Double, quantity: Double) {
        viewModelScope.launch {
            val existing = holdingDao.getHolding(assetId)
            val wallet = walletDao.getWalletOnce() ?: WalletEntity()

            if (quantity <= 0) {
                _uiState.update { it.copy(tradeResult = TradeResult.Error("Enter a valid quantity")) }
                return@launch
            }
            if (existing == null || quantity > existing.quantity) {
                _uiState.update { it.copy(tradeResult = TradeResult.Error("Insufficient holdings")) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            val newQty = existing.quantity - quantity
            if (newQty < 0.0001) {
                holdingDao.deleteHolding(assetId)
            } else {
                holdingDao.upsertHolding(existing.copy(quantity = newQty))
            }

            val proceeds = price * quantity
            walletDao.updateBalanceAndSeedIfMissing(wallet.cashBalance + proceeds)
            tradeDao.insertTrade(TradeEntity(assetId = assetId, type = TradeType.SELL, quantity = quantity, price = price))

            _uiState.update {
                it.copy(
                    isLoading = false,
                    cashBalance = wallet.cashBalance + proceeds,
                    currentHolding = if (newQty < 0.0001) 0.0 else newQty,
                    tradeResult = TradeResult.Success
                )
            }
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(tradeResult = null) }
    }
}
