package app.krafted.tradeacademy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.tradeacademy.data.Asset
import app.krafted.tradeacademy.data.AssetRepository
import app.krafted.tradeacademy.engine.PriceSimulator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MarketUiState(
    val assets: List<Asset> = emptyList(),
    val livePrices: Map<String, Double> = emptyMap(),
    val selectedCategory: String? = null
)

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssetRepository(application)

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    private var assetMap: Map<String, Asset> = emptyMap()

    init {
        loadAssets()
        startPriceTick()
    }

    private fun loadAssets() {
        viewModelScope.launch {
            val assets = repository.getAssets()
            assetMap = assets.associateBy { it.id }
            val initialPrices = assets.associate { it.id to it.basePrice }
            _uiState.update { it.copy(assets = assets, livePrices = initialPrices) }
        }
    }

    private fun startPriceTick() {
        viewModelScope.launch {
            while (true) {
                delay(30_000L)
                _uiState.update { state ->
                    val updatedPrices = state.livePrices.mapValues { (id, currentPrice) ->
                        val asset = assetMap[id]
                        if (asset != null) PriceSimulator.simulateTick(currentPrice, asset.volatility)
                        else currentPrice
                    }
                    state.copy(livePrices = updatedPrices)
                }
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
