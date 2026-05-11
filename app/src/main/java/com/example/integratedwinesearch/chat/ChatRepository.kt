package com.example.integratedwinesearch.chat

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ChatRepository {

    private val client = OkHttpClient()
    private val endpoint = "https://strain-republicans-receipt-laptop.trycloudflare.com/sommelier"
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun sendMessage(
        message: String,
        callback: (Boolean, String?, String?) -> Unit
    ) {
        val bodyJson = JSONObject().apply {
            put("message", message)
        }

        val request = Request.Builder()
            .url(endpoint)
            .post(bodyJson.toString().toRequestBody(jsonType))
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                callback(false, null, e.message ?: "네트워크 오류")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    callback(false, null, "서버 오류: ${response.code}")
                    return
                }

                try {
                    val root = JSONObject(body)
                    val reply = root.optString("reply")
                    if (reply.isBlank()) {
                        callback(false, null, "응답 메시지가 비어있습니다.")
                    } else {
                        callback(true, reply, null)
                    }
                } catch (e: Exception) {
                    callback(false, null, "응답 파싱 실패")
                }
            }
        })
    }
}

