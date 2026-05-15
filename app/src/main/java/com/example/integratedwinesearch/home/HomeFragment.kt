package com.example.integratedwinesearch.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.integratedwinesearch.MainActivity
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.adapter.CategoryAdapter
import com.example.integratedwinesearch.CategoryRepository
import com.example.integratedwinesearch.adapter.BestSellerAdapter
import com.example.integratedwinesearch.adapter.RecentWineAdapter
import com.example.integratedwinesearch.RecentWineRepository
import com.example.integratedwinesearch.adapter.RecommendAdapter
import com.example.integratedwinesearch.RecommendRepository
import com.example.integratedwinesearch.WineDetailActivity
import com.example.integratedwinesearch.AuthSession
import com.example.integratedwinesearch.model.WineItem
import com.example.integratedwinesearch.wine.WineRepository

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var bannerViewPager: ViewPager2
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var bestSellerRecyclerView: RecyclerView
    private lateinit var recommendRecyclerView: RecyclerView
    private lateinit var recentRecyclerView: RecyclerView

    private val homeRepository = HomeRepository()
    private val categoryRepository = CategoryRepository()
    private val recommendRepository = RecommendRepository()
    private val recentWineRepository = RecentWineRepository()
    private val wineRepository = WineRepository()

    private val sliderHandler = Handler(Looper.getMainLooper())

    private val sliderRunnable = object : Runnable {
        override fun run() {
            if (!::bannerViewPager.isInitialized) return

            val adapter = bannerViewPager.adapter ?: return
            if (adapter.itemCount == 0) return

            val nextItem = (bannerViewPager.currentItem + 1) % adapter.itemCount
            bannerViewPager.setCurrentItem(nextItem, true)
            sliderHandler.postDelayed(this, 3000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initBanner()
        initCategory()
        initBestSeller()
        initRecommend()
        initRecentWine()
    }

    private fun initViews(view: View) {
        bannerViewPager = view.findViewById(R.id.bannerViewPager)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        bestSellerRecyclerView = view.findViewById(R.id.bestSellerRecyclerView)
        recommendRecyclerView = view.findViewById(R.id.recommendRecyclerView)
        recentRecyclerView = view.findViewById(R.id.recentRecyclerView)
    }

    private fun initBanner() {
        val bannerItems = homeRepository.getBannerItems()
        bannerViewPager.adapter = BannerAdapter(bannerItems)
    }

    private fun initCategory() {
        val categoryItems = categoryRepository.getCategoryItems()
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.adapter = CategoryAdapter(categoryItems) { category ->
            (activity as? MainActivity)?.openSearchWithCategory(category.name)
        }

        categoryRecyclerView.clipToPadding = false
        categoryRecyclerView.doOnLayout {
            val itemWidthPx = resources.displayMetrics.density * 70f
            val itemGapPx = resources.displayMetrics.density * 10f
            val contentWidth = (itemWidthPx * categoryItems.size) + (itemGapPx * (categoryItems.size - 1))
            val sidePadding = ((categoryRecyclerView.width - contentWidth) / 2f).toInt().coerceAtLeast(0)
            categoryRecyclerView.setPadding(sidePadding, 0, sidePadding, 0)
        }
    }

    private fun initBestSeller() {
        bestSellerRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        wineRepository.getWinesRanking { success, wines, error ->

            requireActivity().runOnUiThread {
                if (success) {
                    val bestSellerItems = wines.map { mapServerWineToWineItem(it) }
                    bestSellerRecyclerView.adapter = BestSellerAdapter(bestSellerItems) { wine ->
                        openWineDetail(wine)
                    }
                } else {
                    android.util.Log.e("HomeFragment", "BestSeller 조회 실패: $error")
                }
            }
        }
    }

    private fun initRecommend() {
        recommendRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val userId = AuthSession.email.ifBlank { "guest" }
        Log.d("HomeFragment", "Recommend 요청 userId=$userId")

        wineRepository.getRecommendWines(userId = userId) { success, wines, error ->
            requireActivity().runOnUiThread {
                if (success && wines.isNotEmpty()) {
                    val recommendItems = wines.take(4).map {
                        mapServerWineToWineItem(
                            wine = it,
                            description = "당신의 취향 기반 추천"
                        )
                    }
                    recommendRecyclerView.adapter = RecommendAdapter(recommendItems) { wine ->
                        openWineDetail(wine)
                    }
                } else {
                    Log.e("HomeFragment", "Recommend 비어있거나 실패: success=$success, error=$error")
                    wineRepository.getWines(search = null, page = 1, limit = 4) { s2, w2, _ ->
                        requireActivity().runOnUiThread {
                            if (s2 && w2.isNotEmpty()) {
                                val fallbackItems = w2.map {
                                    mapServerWineToWineItem(
                                        wine = it,
                                        description = "당신의 취향 기반 추천"
                                    )
                                }
                                recommendRecyclerView.adapter = RecommendAdapter(fallbackItems) { wine ->
                                    openWineDetail(wine)
                                }
                            } else {
                                recommendRecyclerView.adapter =
                                    RecommendAdapter(recommendRepository.getRecommendItems()) { wine ->
                                        openWineDetail(wine)
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initRecentWine() {
        recentRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        wineRepository.getWines(
            search = null,
            page = 1,
            limit = 3
        ) { success, wines, error ->
            requireActivity().runOnUiThread {
                if (success) {
                    val labels = listOf("방금 전", "1시간 전", "오늘")
                    val recentWineItems = wines.mapIndexed { index, wine ->
                        mapServerWineToWineItem(
                            wine = wine,
                            viewedTime = labels.getOrElse(index) { "최근" }
                        )
                    }
                    recentRecyclerView.adapter = RecentWineAdapter(recentWineItems) { wine ->
                        openWineDetail(wine)
                    }
                } else {
                    Log.e("HomeFragment", "RecentWine 조회 실패: $error")
                    recentRecyclerView.adapter =
                        RecentWineAdapter(recentWineRepository.getRecentWineItems()) { wine ->
                            openWineDetail(wine)
                        }
                }
            }
        }
    }

    private fun openWineDetail(wine: WineItem) {
        val userId = AuthSession.email.ifBlank { "guest" }
        wineRepository.saveUserViewLog(
            userId = userId,
            wineName = wine.name,
            category = wine.type
        )

        val intent = WineDetailActivity.createIntent(
            context = requireContext(),
            mode = WineDetailActivity.MODE_SEARCH,
            wineName = wine.name,
            wineType = wine.type,
            wineRegion = "프랑스 보르도",
            wineGrade = wine.grade,
            winePrice = wine.price,
            wineImageUrl = wine.imageUrl
        )
        startActivity(intent)
    }

    private fun mapServerWineToWineItem(
        wine: com.example.integratedwinesearch.wine.model.ServerWineItem,
        description: String? = null,
        viewedTime: String? = null
    ): WineItem {
        Log.d("WINE_IMAGE", "mapped image_url = ${wine.imageUrl}")
        val fullImageUrl = normalizeImageUrl(wine.imageUrl)
        return WineItem(
            id = wine.id.toString(),
            type = wine.category,
            grade = "A",
            name = wine.name,
            price = wine.price,
            imageResId = R.drawable.sample_wine_red,
            imageUrl = fullImageUrl,
            description = description,
            viewedTime = viewedTime
        )
    }

    private fun normalizeImageUrl(imageUrl: String?): String? {
        if (imageUrl.isNullOrBlank()) return null
        return if (imageUrl.startsWith("None/")) {
            "http://15.164.220.246:8000/" + imageUrl.replace("None/", "images/")
        } else {
            imageUrl
        }
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}
