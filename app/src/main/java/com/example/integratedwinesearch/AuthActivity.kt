package com.example.integratedwinesearch

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AuthActivity : AppCompatActivity() {

    private val baseUrl = "https://marketplace-conclusions-feet-sectors.trycloudflare.com"

    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private lateinit var tvLoginTab: TextView
    private lateinit var tvSignupTab: TextView
    private lateinit var layoutLoginForm: LinearLayout
    private lateinit var layoutSignupForm: LinearLayout
    private lateinit var btnSubmit: MaterialButton
    private lateinit var tvBottomGuide: TextView

    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var checkKeepLogin: CheckBox

    private lateinit var etSignupName: EditText
    private lateinit var etSignupPhone: EditText
    private lateinit var etSignupEmail: EditText
    private lateinit var etSignupPassword: EditText

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initEvents()
        setMode(true)
    }

    private fun initViews() {
        tvLoginTab = findViewById(R.id.tvLoginTab)
        tvSignupTab = findViewById(R.id.tvSignupTab)
        layoutLoginForm = findViewById(R.id.layoutLoginForm)
        layoutSignupForm = findViewById(R.id.layoutSignupForm)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvBottomGuide = findViewById(R.id.tvBottomGuide)

        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        checkKeepLogin = findViewById(R.id.checkKeepLogin)

        etSignupName = findViewById(R.id.etSignupName)
        etSignupPhone = findViewById(R.id.etSignupPhone)
        etSignupEmail = findViewById(R.id.etSignupEmail)
        etSignupPassword = findViewById(R.id.etSignupPassword)
    }

    private fun initEvents() {
        tvLoginTab.setOnClickListener { setMode(true) }
        tvSignupTab.setOnClickListener { setMode(false) }

        btnSubmit.setOnClickListener {
            if (isLoginMode) login() else register()
        }
    }

    private fun setMode(loginMode: Boolean) {
        isLoginMode = loginMode

        if (loginMode) {
            tvLoginTab.setBackgroundResource(R.drawable.bg_auth_tab_selected)
            tvSignupTab.background = null

            tvLoginTab.setTextColor(getColor(R.color.auth_red))
            tvSignupTab.setTextColor(getColor(R.color.auth_gray))

            layoutLoginForm.visibility = View.VISIBLE
            layoutSignupForm.visibility = View.GONE

            btnSubmit.text = "로그인"
            tvBottomGuide.visibility = View.GONE
        } else {
            tvSignupTab.setBackgroundResource(R.drawable.bg_auth_tab_selected)
            tvLoginTab.background = null

            tvSignupTab.setTextColor(getColor(R.color.auth_red))
            tvLoginTab.setTextColor(getColor(R.color.auth_gray))

            layoutLoginForm.visibility = View.GONE
            layoutSignupForm.visibility = View.VISIBLE

            btnSubmit.text = "회원가입"
            tvBottomGuide.visibility = View.VISIBLE
        }
    }

    private fun login() {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            toast("이메일과 비밀번호를 입력해주세요.")
            return
        }

        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        postJson("$baseUrl/login", json) { success, body ->
            if (success) {
                val responseJson = JSONObject(body)
                val token = responseJson.getString("access_token")
                val tokenType = responseJson.optString("token_type", "bearer")
                val userName = email.substringBefore("@")
                val keepLogin = checkKeepLogin.isChecked

                AuthSession.isLoggedIn = true
                AuthSession.accessToken = token
                AuthSession.tokenType = tokenType
                AuthSession.email = email
                AuthSession.userName = userName

                val prefs = getSharedPreferences("auth", MODE_PRIVATE)

                if (keepLogin) {
                    prefs.edit {
                        putBoolean("keepLogin", true)
                        putBoolean("isLoggedIn", true)
                        putString("accessToken", token)
                        putString("tokenType", tokenType)
                        putString("email", email)
                        putString("userName", userName)
                    }
                } else {
                    prefs.edit { clear() }
                }

                toast("로그인 성공")
                finish()
            } else {
                toast("로그인 실패: $body")
            }
        }
    }

    private fun register() {
        val name = etSignupName.text.toString().trim()
        val phone = etSignupPhone.text.toString().trim()
        val email = etSignupEmail.text.toString().trim()
        val password = etSignupPassword.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            toast("모든 정보를 입력해주세요.")
            return
        }

        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("phone", phone)
            put("real_name", name)
            put("nickname", name)
            put("birth_date", "2000-01-01")
        }

        postJson("$baseUrl/register", json) { success, body ->
            if (success) {
                toast("회원가입 완료. 로그인해주세요.")
                runOnUiThread {
                    setMode(true)
                    etLoginEmail.setText(email)
                    etLoginPassword.setText(password)
                }
            } else {
                toast("회원가입 실패: $body")
            }
        }
    }

    private fun postJson(url: String, json: JSONObject, callback: (Boolean, String) -> Unit) {
        val requestBody = json.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message ?: "네트워크 오류")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string().orEmpty()
                callback(response.isSuccessful, body)
            }
        })
    }

    private fun toast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}