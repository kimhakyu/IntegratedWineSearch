package com.example.integratedwinesearch.wine

import android.net.Uri
import android.util.Log
import com.example.integratedwinesearch.wine.model.ServerWineItem
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType

class WineRepository {

    private val client = OkHttpClient()
    private val baseUrl = "http://15.164.220.246:8000"
    private val tag = "WineRepository"
    private val jsonType = "application/json; charset=utf-8"

    fun getWines(
        search: String? = null,
        page: Int = 1,
        limit: Int = 10,
        callback: (Boolean, List<ServerWineItem>, String?) -> Unit
    ) {
        val urlBuilder = Uri.parse("$baseUrl/api/wines").buildUpon()
            .appendQueryParameter("page", page.toString())
            .appendQueryParameter("limit", limit.toString())

        if (!search.isNullOrBlank()) {
            urlBuilder.appendQueryParameter("search", search)
        }

        val request = Request.Builder()
            .url(urlBuilder.build().toString())
            .get()
            .build()

        Log.d(tag, "GET ${request.url}")

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e(tag, "API 실패: ${e.message}", e)
                callback(false, emptyList(), e.message)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    callback(false, emptyList(), body)
                    return
                }

                val wines = parseWineList(body)
                callback(true, wines, null)
            }
        })
    }

    fun getWinesRanking(
        callback: (Boolean, List<ServerWineItem>, String?) -> Unit
    ) {
        val request = Request.Builder()
            .url("$baseUrl/api/wines/ranking")
            .get()
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                callback(false, emptyList(), e.message)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    callback(false, emptyList(), body)
                    return
                }
                callback(true, parseWineList(body), null)
            }
        })
    }

    fun getRecommendWines(
        userId: String,
        callback: (Boolean, List<ServerWineItem>, String?) -> Unit
    ) {
        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegment("api")
            .addPathSegment("wines")
            .addPathSegment("recommend")
            .addPathSegment(userId)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                callback(false, emptyList(), e.message)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    callback(false, emptyList(), body)
                    return
                }
                Log.d(tag, "recommend userId=$userId response=$body")
                callback(true, parseWineList(body), null)
            }
        })
    }

    fun saveUserViewLog(
        userId: String,
        wineName: String,
        category: String,
        callback: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val payload = JSONObject()
            .put("user_id", userId)
            .put("wine_nm", wineName)
            .put("category", category)

        val request = Request.Builder()
            .url("$baseUrl/api/wines/user_view")
            .post(payload.toString().toRequestBody(jsonType.toMediaType()))
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    callback(false, response.body?.string().orEmpty())
                    return
                }
                callback(true, null)
            }
        })
    }

    private fun parseWineList(body: String): List<ServerWineItem> {
        val trimmed = body.trim()
        if (trimmed.isBlank()) return emptyList()

        return try {
            when {
                trimmed.startsWith("{") -> parseFromObject(JSONObject(trimmed))
                trimmed.startsWith("[") -> parseFromArray(JSONArray(trimmed))
                trimmed.startsWith("\"") -> {
                    val decoded = JSONObject("{\"v\":$trimmed}").optString("v")
                    parseWineList(decoded)
                }
                else -> emptyList()
            }
        } catch (e: Exception) {
            Log.e(tag, "응답 파싱 실패: ${e.message}")
            emptyList()
        }
    }

    private fun parseFromObject(root: JSONObject): List<ServerWineItem> {
        val dataArray = root.optJSONArray("data")
        if (dataArray != null) return parseFromArray(dataArray)

        val itemsArray = root.optJSONArray("items")
        if (itemsArray != null) return parseFromArray(itemsArray)

        return if (looksLikeWine(root)) listOf(mapWine(root)) else emptyList()
    }

    private fun parseFromArray(array: JSONArray): List<ServerWineItem> {
        val result = mutableListOf<ServerWineItem>()
        for (i in 0 until array.length()) {
            val value = array.opt(i)
            when (value) {
                is JSONObject -> {
                    if (looksLikeWine(value)) {
                        result.add(mapWine(value))
                    }
                }
                is String -> {
                    val parsed = parseWineList(value)
                    result.addAll(parsed)
                }
            }
        }
        return result
    }

    private fun looksLikeWine(item: JSONObject): Boolean {
        return item.has("WINE_ID") ||
            item.has("wine_id") ||
            item.has("WINE_NM") ||
            item.has("wine_nm") ||
            item.has("image_url")
    }

    private fun mapWine(item: JSONObject): ServerWineItem {
        val id = item.optInt("WINE_ID", item.optInt("wine_id", 0))
        val name = item.optString("WINE_NM", item.optString("wine_nm", ""))
        val area = item.optString("WINE_AREA_NM", item.optString("wine_area_nm", ""))
        val category = item.optString("WINE_CTGRY", item.optString("wine_ctgry", ""))
        val price = item.optInt("WINE_PRC", item.optInt("wine_prc", 0))
        val imageUrl = item.optString("image_url", "")

        return ServerWineItem(
            id = id,
            name = name,
            area = area,
            category = category,
            price = price,
            imageUrl = imageUrl
        )
    }
}
