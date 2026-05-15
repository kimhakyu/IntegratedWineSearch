package com.example.integratedwinesearch

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.integratedwinesearch.chat.ChatFragment
import com.example.integratedwinesearch.home.HomeFragment
import com.example.integratedwinesearch.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var lastImeVisible = false
    private var pendingSearchCategory: String? = null
    private var pendingSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView)?.let {
            it.isAppearanceLightNavigationBars = true
        }

        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNav)

        // 기존 기능 유지: 시스템 네비게이션 바 높이만큼 bottomNav 위치 조정
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            val lp = view.layoutParams as ViewGroup.MarginLayoutParams
            lp.bottomMargin = navBar.bottom
            view.layoutParams = lp

            insets
        }
        val rootContent = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootContent) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            if (imeVisible != lastImeVisible) {
                lastImeVisible = imeVisible

                if (imeVisible) {
                    bottomNav.animate()
                        .translationY(bottomNav.height.toFloat() + 100f)
                        .alpha(0f)
                        .setDuration(180)
                        .withStartAction {
                            bottomNav.isClickable = false
                            bottomNav.isEnabled = false
                        }
                        .withEndAction {
                            bottomNav.visibility = View.GONE
                        }
                        .start()
                } else {
                    bottomNav.visibility = View.VISIBLE
                    bottomNav.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(180)
                        .withStartAction {
                            bottomNav.isClickable = true
                            bottomNav.isEnabled = true
                        }
                        .start()
                }
            }

            insets
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.nav_search -> {
                    val fragment = SearchFragment.newInstance(
                        preselectCategory = pendingSearchCategory,
                        prefillQuery = pendingSearchQuery
                    )
                    pendingSearchCategory = null
                    pendingSearchQuery = null
                    replaceFragment(fragment)
                    true
                }

                R.id.nav_ai_scan -> {
                    replaceFragment(AiFragment())
                    true
                }

                R.id.nav_chatbot -> {
                    replaceFragment(ChatFragment())
                    true
                }

                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun openSearchWithCategory(categoryName: String) {
        pendingSearchCategory = categoryName
        bottomNav.selectedItemId = R.id.nav_search
    }

    fun openSearchWithQuery(query: String) {
        pendingSearchQuery = query
        pendingSearchCategory = null
        bottomNav.selectedItemId = R.id.nav_search
    }
}
