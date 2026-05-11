package com.example.integratedwinesearch.wine

import android.net.Uri
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.example.integratedwinesearch.wine.model.ServerWineItem

class WineRepository {

    private val client = OkHttpClient()
    private val baseUrl = "http://15.164.220.246:8000"
    private val tag = "WineRepository"

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

                val root = JSONObject(body)
                val dataArray = root.optJSONArray("data")

                val wines = mutableListOf<ServerWineItem>()

                if (dataArray != null) {
                    for (i in 0 until dataArray.length()) {
                        val item = dataArray.getJSONObject(i)

                        wines.add(
                            ServerWineItem(
                                id = item.optInt("WINE_ID"),
                                name = item.optString("WINE_NM"),
                                area = item.optString("WINE_AREA_NM"),
                                category = item.optString("WINE_CTGRY"),
                                price = item.optInt("WINE_PRC"),
                                imageUrl = item.optString("image_url")
                            )
                        )
                    }
                }

                wines.take(10).forEachIndexed { index, wine ->
                    Log.d(
                        tag,
                        "image[$index] id=${wine.id}, name=${wine.name}, raw_image_url=${wine.imageUrl}"
                    )
                }
                verifyImageUrls(wines.take(10).mapNotNull { it.imageUrl })

                callback(true, wines, null)
            }
        })
    }

    private fun verifyImageUrls(urls: List<String>) {
        urls.forEachIndexed { index, url ->
            val request = Request.Builder()
                .url(url)
                .head()
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    Log.e(tag, "image_check[$index] FAIL url=$url, error=${e.message}")
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    response.use {
                        Log.d(
                            tag,
                            "image_check[$index] code=${it.code}, success=${it.isSuccessful}, url=$url"
                        )
                    }
                }
            })
        }
    }
}
