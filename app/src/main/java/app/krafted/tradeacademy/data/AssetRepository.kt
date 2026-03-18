package app.krafted.tradeacademy.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetRepository(context: Context) {

    private val appContext = context.applicationContext
    private val gson = Gson()

    suspend fun getAssets(): List<Asset> = withContext(Dispatchers.IO) {
        try {
            val json = appContext.assets.open("market.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Asset>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getArticles(): List<Article> = withContext(Dispatchers.IO) {
        try {
            val json = appContext.assets.open("news.json").bufferedReader().use { it.readText() }
            gson.fromJson(json, NewsResponse::class.java).articles
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTips(): List<Tip> = withContext(Dispatchers.IO) {
        try {
            val json = appContext.assets.open("tips.json").bufferedReader().use { it.readText() }
            gson.fromJson(json, TipsResponse::class.java).tips
        } catch (e: Exception) {
            emptyList()
        }
    }
}
