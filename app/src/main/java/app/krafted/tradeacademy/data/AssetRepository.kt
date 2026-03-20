package app.krafted.tradeacademy.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetRepository(context: Context) {

    private val appContext = context.applicationContext
    private val gson = Gson()

    private var cachedAssets: List<Asset>? = null
    private var cachedArticles: List<Article>? = null
    private var cachedTips: List<Tip>? = null

    suspend fun getAssets(): List<Asset> {
        cachedAssets?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val json = appContext.assets.open("market.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Asset>>() {}.type
                gson.fromJson<List<Asset>>(json, type)
            } catch (e: Exception) {
                Log.e("AssetRepository", "Failed to load assets", e)
                emptyList()
            }
        }.also { cachedAssets = it }
    }

    suspend fun getArticles(): List<Article> {
        cachedArticles?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val json = appContext.assets.open("news.json").bufferedReader().use { it.readText() }
                gson.fromJson(json, NewsResponse::class.java).articles
            } catch (e: Exception) {
                Log.e("AssetRepository", "Failed to load articles", e)
                emptyList()
            }
        }.also { cachedArticles = it }
    }

    suspend fun getTips(): List<Tip> {
        cachedTips?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val json = appContext.assets.open("tips.json").bufferedReader().use { it.readText() }
                gson.fromJson(json, TipsResponse::class.java).tips
            } catch (e: Exception) {
                Log.e("AssetRepository", "Failed to load tips", e)
                emptyList()
            }
        }.also { cachedTips = it }
    }
}
