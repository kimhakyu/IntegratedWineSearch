package com.example.integratedwinesearch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutLoginBanner = view.findViewById(R.id.layoutLoginBanner)
        bannerIconContainer = view.findViewById(R.id.bannerIconContainer)
        ivBannerUserState = view.findViewById(R.id.ivBannerUserState)
        tvBannerTitle = view.findViewById(R.id.tvBannerTitle)
        tvBannerSubTitle = view.findViewById(R.id.tvBannerSubTitle)
        tvLogout = view.findViewById(R.id.tvLogout)

        // 테스트용
        val isLoggedIn = false
        val userName = "학유"

        updateLoginBanner(isLoggedIn, userName)

        // 나중에 비로그인 상태면 로그인 화면 이동
        view.findViewById<View>(R.id.cardLoginBanner).setOnClickListener {
            if (!isLoggedIn) {
                // 로그인 / 회원가입 화면 이동
            } else {
                // 마이페이지나 계정 정보 화면 이동
            }
        }
    }

    private fun updateLoginBanner(isLoggedIn: Boolean, userName: String) {
        if (isLoggedIn) {
            layoutLoginBanner.setBackgroundResource(R.drawable.bg_settings_banner_logged_in)
            bannerIconContainer.setBackgroundResource(R.drawable.bg_banner_icon_green_circle)
            ivBannerUserState.setImageResource(R.drawable.ic_user_purple)
            tvBannerTitle.text = "${userName}님"
            tvBannerSubTitle.text = "환영합니다"
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