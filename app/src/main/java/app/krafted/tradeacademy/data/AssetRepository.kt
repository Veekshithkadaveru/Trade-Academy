package app.krafted.tradeacademy.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AssetRepository(private val context: Context) {

    private val gson = Gson()

    fun getAssets(): List<Asset> {
        val json = context.assets.open("market.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Asset>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getArticles(): List<Article> {
        val json = context.assets.open("news.json").bufferedReader().use { it.readText() }
        val response = gson.fromJson(json, NewsResponse::class.java)
        return response.articles
    }

    fun getTips(): List<Tip> {
        val json = context.assets.open("tips.json").bufferedReader().use { it.readText() }
        val response = gson.fromJson(json, TipsResponse::class.java)
        return response.tips
    }
}
