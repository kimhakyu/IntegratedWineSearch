package com.example.integratedwinesearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var layoutLoginBanner: LinearLayout
    private lateinit var bannerIconContainer: FrameLayout
    private lateinit var ivBannerUserState: ImageView
    private lateinit var tvBannerTitle: TextView
    private lateinit var tvBannerSubTitle: TextView
    private lateinit var tvLogout: TextView

    private var isLoggedIn = false
    private var userName = "사용자"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initEvents(view)
    }

    override fun onResume() {
        super.onResume()
        loadLoginState()
        updateLoginBanner(isLoggedIn, userName)
    }

    private fun initViews(view: View) {
        layoutLoginBanner = view.findViewById(R.id.layoutLoginBanner)
        bannerIconContainer = view.findViewById(R.id.bannerIconContainer)
        ivBannerUserState = view.findViewById(R.id.ivBannerUserState)
        tvBannerTitle = view.findViewById(R.id.tvBannerTitle)
        tvBannerSubTitle = view.findViewById(R.id.tvBannerSubTitle)
        tvLogout = view.findViewById(R.id.tvLogout)
    }

    private fun initEvents(view: View) {
        view.findViewById<View>(R.id.cardLoginBanner).setOnClickListener {
            if (!isLoggedIn) {
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            } else {
                view.findViewById<View>(R.id.cardLoginBanner).setOnClickListener {
                    if (!isLoggedIn) {
                        startActivity(Intent(requireContext(), AuthActivity::class.java))
                    } else {
                        startActivity(Intent(requireContext(), ProfileActivity::class.java))
                    }
                }
            }
        }

        tvLogout.setOnClickListener {
            requireContext()
                .getSharedPreferences("auth", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            AuthSession.clear()

            isLoggedIn = false
            userName = "사용자"
            updateLoginBanner(isLoggedIn, userName)
        }
    }

    private fun loadLoginState() {
        val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val keepLogin = prefs.getBoolean("keepLogin", false)

        if (keepLogin && prefs.getBoolean("isLoggedIn", false)) {
            isLoggedIn = true
            userName = prefs.getString("userName", null)
                ?: prefs.getString("email", "사용자")
                        ?: "사용자"
        } else {
            isLoggedIn = AuthSession.isLoggedIn
            userName = AuthSession.userName
        }
    }

    private fun updateLoginBanner(isLoggedIn: Boolean, userName: String) {
        if (isLoggedIn) {
            layoutLoginBanner.setBackgroundResource(R.drawable.bg_settings_banner_logged_in)
            bannerIconContainer.setBackgroundResource(R.drawable.bg_banner_icon_green_circle)
            ivBannerUserState.setImageResource(R.drawable.ic_user_purple)

            tvBannerTitle.text = "회원정보 확인"
            tvBannerSubTitle.text = "${userName}님 환영합니다"
            tvLogout.visibility = View.VISIBLE
        } else {
            layoutLoginBanner.setBackgroundResource(R.drawable.bg_settings_banner_logged_out)
            bannerIconContainer.setBackgroundResource(R.drawable.bg_banner_icon_red_circle)
            ivBannerUserState.setImageResource(R.drawable.ic_lock_red)

            tvBannerTitle.text = "로그인 / 회원가입"
            tvBannerSubTitle.text = "더 많은 기능을 이용하려면 로그인하세요"
            tvLogout.visibility = View.GONE
        }
    }
}