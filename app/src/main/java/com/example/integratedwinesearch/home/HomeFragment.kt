package com.example.integratedwinesearch.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.adapter.CategoryAdapter
import com.example.integratedwinesearch.CategoryRepository
import com.example.integratedwinesearch.adapter.BestSellerAdapter
import com.example.integratedwinesearch.BestSellerRepository
import com.example.integratedwinesearch.adapter.RecentWineAdapter
import com.example.integratedwinesearch.RecentWineRepository
import com.example.integratedwinesearch.adapter.RecommendAdapter
import com.example.integratedwinesearch.RecommendRepository

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var bannerViewPager: ViewPager2
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var bestSellerRecyclerView: RecyclerView
    private lateinit var recommendRecyclerView: RecyclerView
    private lateinit var recentRecyclerView: RecyclerView

    private val homeRepository = HomeRepository()
    private val categoryRepository = CategoryRepository()
    private val bestSellerRepository = BestSellerRepository()
    private val recommendRepository = RecommendRepository()
    private val recentWineRepository = RecentWineRepository()

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
        categoryRecyclerView.adapter = CategoryAdapter(categoryItems)
    }

    private fun initBestSeller() {
        val bestSellerItems = bestSellerRepository.getBestSellerItems()
        bestSellerRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        bestSellerRecyclerView.adapter = BestSellerAdapter(bestSellerItems)
    }

    private fun initRecommend() {
        val recommendItems = recommendRepository.getRecommendItems()
        recommendRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recommendRecyclerView.adapter = RecommendAdapter(recommendItems)
    }

    private fun initRecentWine() {
        val recentWineItems = recentWineRepository.getRecentWineItems()
        recentRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recentRecyclerView.adapter = RecentWineAdapter(recentWineItems)
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