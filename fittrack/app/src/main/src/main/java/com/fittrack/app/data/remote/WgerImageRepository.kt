package com.fittrack.app.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WgerImageRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("wger_image_cache", Context.MODE_PRIVATE)
    private val mutex = Mutex()
    private val memoryCache = mutableMapOf<String, String?>()

    suspend fun getImageUrl(exerciseName: String): String? {
        val cacheKey = exerciseName.lowercase().trim()

        memoryCache[cacheKey]?.let { return it }

        val cached = prefs.getString(cacheKey, null)
        if (cached != null) {
            val url = cached.ifEmpty { null }
            memoryCache[cacheKey] = url
            return url
        }

        return mutex.withLock {
            memoryCache[cacheKey]?.let { return it }
            val url = fetchFromWger(exerciseName)
            prefs.edit().putString(cacheKey, url ?: "").apply()
            memoryCache[cacheKey] = url
            url
        }
    }

    private suspend fun fetchFromWger(exerciseName: String): String? = withContext(Dispatchers.IO) {
        try {
            val encoded = URLEncoder.encode(exerciseName, "UTF-8")
            val url = URL("$BASE_URL/api/v2/exercise/search/?term=$encoded&language=english&format=json")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"

            if (conn.responseCode != 200) return@withContext null

            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            val json = JSONObject(body)
            val suggestions = json.getJSONArray("suggestions")

            for (i in 0 until suggestions.length()) {
                val data = suggestions.getJSONObject(i).getJSONObject("data")
                if (data.isNull("image")) continue
                val imagePath = data.getString("image")
                if (imagePath.isNotEmpty()) {
                    return@withContext "$BASE_URL$imagePath"
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val BASE_URL = "https://wger.de"
    }
}
