package app.krafted.tradeacademy.ui

import androidx.compose.ui.graphics.Color

fun categoryColor(category: String): Color = when (category) {
    "Stocks" -> Color(0xFF2196F3)
    "Crypto" -> Color(0xFFF7931A)
    "Forex" -> Color(0xFF9C27B0)
    "Commodities" -> Color(0xFFFFD700)
    "Market" -> Color(0xFF2196F3)
    else -> Color(0xFF607D8B)
}
