package com.example.integratedwinesearch

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private val baseUrl = "https://marketplace-conclusions-feet-sectors.trycloudflare.com"
    private val client = OkHttpClient()

    private lateinit var btnBack: ImageButton
    private lateinit var btnEditOrSave: ImageButton
    private lateinit var btnCancel: MaterialButton

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileSub: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initEvents()
        loadProfileFromServer()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEditOrSave = findViewById(R.id.btnEditOrSave)
        btnCancel = findViewById(R.id.btnCancel)

        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileSub = findViewById(R.id.tvProfileSub)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnEditOrSave.setOnClickListener {
            // TODO: 수정 모드 / 저장 기능
        }
    }

    private fun loadProfileFromServer() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)

        val token = prefs.getString("accessToken", null)
            ?: AuthSession.accessToken

        if (token.isNullOrBlank()) {
            toast("로그인 정보가 없습니다.")
            finish()
            return
        }

        val request = Request.Builder()
            .url("$baseUrl/profile")
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                toast("회원 정보를 불러오지 못했습니다.")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    toast("회원 정보 조회 실패: $body")
                    return
                }

                val json = JSONObject(body)

                val email = json.optString("email", "이메일 정보 없음")
                val phone = json.optString("phone", "전화번호 정보 없음")
                val realName = json.optString("real_name", "사용자")
                val nickname = json.optString("nickname", realName)
                val birthDate = json.optString("birth_date", "")

                runOnUiThread {
                    tvProfileName.text = realName
                    tvProfileSub.text = "Wine Manager 회원"

                    setupProfileRow(
                        rowId = R.id.rowName,
                        label = "이름",
                        value = realName,
                        iconRes = R.drawable.ic_user_purple
                    )

                    setupProfileRow(
                        rowId = R.id.rowEmail,
                        label = "이메일",
                        value = email,
                        iconRes = R.drawable.baseline_attach_email_24
                    )

                    setupProfileRow(
                        rowId = R.id.rowPhone,
                        label = "전화번호",
                        value = phone,
                        iconRes = R.drawable.ic_smartphone_red
                    )

                    prefs.edit()
                        .putString("userName", realName)
                        .putString("email", email)
                        .putString("phone", phone)
                        .putString("nickname", nickname)
                        .putString("birthDate", birthDate)
                        .apply()

                    AuthSession.userName = realName
                    AuthSession.email = email
                }
            }
        })
    }

    private fun setupProfileRow(
        rowId: Int,
        label: String,
        value: String,
        iconRes: Int
    ) {
        val row = findViewById<View>(rowId)

        val tvLabel = row.findViewById<TextView>(R.id.tvProfileLabel)
        val tvValue = row.findViewById<TextView>(R.id.tvProfileValue)
        val ivIcon = row.findViewById<ImageView>(R.id.ivProfileIcon)

        tvLabel.text = label
        tvValue.text = value
        ivIcon.setImageResource(iconRes)
    }

    private fun toast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}