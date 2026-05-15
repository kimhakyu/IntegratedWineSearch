package com.example.integratedwinesearch.ai

import com.example.integratedwinesearch.ai.model.OcrSearchResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class OcrRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .build()
    private val endpoint = "https://strain-republicans-receipt-laptop.trycloudflare.com/wine/search"

    fun searchWineByImage(
        imageFile: File,
        callback: (Boolean, List<OcrSearchResult>, String?) -> Unit
    ) {
        val mediaType = "image/*".toMediaType()
        val fileBody = imageFile.asRequestBody(mediaType)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.name, fileBody)
            .build()

        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                val msg = if (e is SocketTimeoutException) {
                    "요청 시간 초과: 이미지 용량을 줄여 다시 시도해주세요."
                } else {
                    e.message ?: "네트워크 오류"
                }
                callback(false, emptyList(), msg)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    callback(false, emptyList(), "서버 오류: ${response.code}")
                    return
                }

                try {
                    val root = JSONObject(body)
                    val array = root.optJSONArray("results")
                    val results = mutableListOf<OcrSearchResult>()

                    if (array != null) {
                        for (i in 0 until array.length()) {
                            val item = array.optJSONObject(i) ?: continue
                            results.add(
                                OcrSearchResult(
                                    title = item.optString("title"),
                                    link = item.optString("link"),
                                    thumbnail = item.optString("thumbnail")
                                )
                            )
                        }
                    }

                    callback(true, results, null)
                } catch (e: Exception) {
                    callback(false, emptyList(), "응답 파싱 실패")
                }
            }
        })
    }
}
