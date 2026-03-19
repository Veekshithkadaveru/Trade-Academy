package app.krafted.tradeacademy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.tradeacademy.data.Article
import app.krafted.tradeacademy.data.AssetRepository
import app.krafted.tradeacademy.data.Tip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContentUiState(
    val articles: List<Article> = emptyList(),
    val tips: List<Tip> = emptyList()
)

class ContentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssetRepository(application)

    private val _uiState = MutableStateFlow(ContentUiState())
    val uiState: StateFlow<ContentUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch {
            val articles = repository.getArticles()
            val tips = repository.getTips()
            _uiState.update { it.copy(articles = articles, tips = tips) }
        }
    }
}
