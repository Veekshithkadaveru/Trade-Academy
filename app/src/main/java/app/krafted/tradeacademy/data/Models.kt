package app.krafted.tradeacademy.data

data class Asset(
    val id: String,
    val name: String,
    val category: String,
    val basePrice: Double,
    val volatility: Double
)

data class Article(
    val id: Int,
    val category: String,
    val headline: String,
    val summary: String,
    val body: String,
    val tag: String,
    val date: String
)

data class Tip(
    val id: Int,
    val icon: String,
    val difficulty: String,
    val title: String,
    val body: String
)

data class NewsResponse(val articles: List<Article>)
data class TipsResponse(val tips: List<Tip>)
