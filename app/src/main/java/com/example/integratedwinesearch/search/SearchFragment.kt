package com.example.integratedwinesearch.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.integratedwinesearch.AuthSession
import com.example.integratedwinesearch.NfcScanActivity
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.WineDetailActivity
import com.example.integratedwinesearch.search.model.SearchCategoryItem
import com.example.integratedwinesearch.search.model.SearchWineItem
import com.example.integratedwinesearch.wine.WineRepository

class SearchFragment : Fragment(R.layout.fragment_search) {

    companion object {
        private const val ARG_PRESELECT_CATEGORY = "arg_preselect_category"
        private const val ARG_PREFILL_QUERY = "arg_prefill_query"

        fun newInstance(
            preselectCategory: String? = null,
            prefillQuery: String? = null
        ): SearchFragment {
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRESELECT_CATEGORY, preselectCategory)
                    putString(ARG_PREFILL_QUERY, prefillQuery)
                }
            }
        }
    }

    private lateinit var searchScrollView: NestedScrollView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var popularWineRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var tvPopularTitle: TextView

    private val searchRepository = SearchRepository()
    private val wineRepository = WineRepository()
    private val wineItems = mutableListOf<SearchWineItem>()
    private lateinit var wineAdapter: SearchWineAdapter

    private var searchQuery: String = ""
    private var selectedCategory: SearchCategoryItem? = null
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var hasMore = true
    private var preselectCategoryName: String? = null
    private var prefillQuery: String? = null
    private var refreshOnResume: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preselectCategoryName = arguments?.getString(ARG_PRESELECT_CATEGORY)
        prefillQuery = arguments?.getString(ARG_PREFILL_QUERY)

        initViews(view)
        initCategory()
        initSearchView()
        applyPrefillQuery()
        fetchPopularWine(isNextPage = false)
    }

    override fun onResume() {
        super.onResume()
        if (refreshOnResume) {
            refreshOnResume = false
            fetchPopularWine(isNextPage = false)
        }
    }

    private fun initViews(view: View) {
        searchScrollView = view.findViewById(R.id.searchScrollView)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        popularWineRecyclerView = view.findViewById(R.id.popularWineRecyclerView)
        searchView = view.findViewById(R.id.searchView)
        tvPopularTitle = view.findViewById(R.id.tvPopularTitle)
        view.findViewById<ImageButton>(R.id.btnSearchAction).setOnClickListener {
            startActivity(NfcScanActivity.createIntent(requireContext()))
        }

        popularWineRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        wineAdapter = SearchWineAdapter(wineItems) { wine ->
            val userId = AuthSession.email.ifBlank { "guest" }
            wineRepository.saveUserViewLog(
                userId = userId,
                wineName = wine.name,
                category = wine.type
            )
            refreshOnResume = true

            val intent = WineDetailActivity.createIntent(
                context = requireContext(),
                mode = WineDetailActivity.MODE_SEARCH,
                wineName = wine.name,
                wineType = wine.type,
                wineRegion = wine.region,
                wineGrade = wine.grade,
                winePrice = wine.price,
                wineImageUrl = wine.imageUrl,
                wineDescription = wine.description
            )
            startActivity(intent)
        }
        popularWineRecyclerView.adapter = wineAdapter

        searchScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                val child = searchScrollView.getChildAt(0) ?: return@OnScrollChangeListener
                val reachedBottom = scrollY >= (child.measuredHeight - searchScrollView.measuredHeight - 120)
                if (reachedBottom && shouldEnablePagination()) {
                    fetchPopularWine(isNextPage = true)
                }
            }
        )
    }

    private fun initCategory() {
        val categoryItems = searchRepository.getSearchCategoryItems()

        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.adapter = SearchCategoryAdapter(categoryItems) { clicked ->
            selectedCategory = if (selectedCategory?.id == clicked.id) null else clicked
            fetchPopularWine(isNextPage = false)
        }

        preselectCategoryName?.let { name ->
            selectedCategory = categoryItems.firstOrNull {
                it.title.contains(name, ignoreCase = true) ||
                    name.contains(it.title.replace(" 와인", ""), ignoreCase = true)
            }
            preselectCategoryName = null
        }
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query.orEmpty().trim()
                fetchPopularWine(isNextPage = false)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    searchQuery = ""
                    fetchPopularWine(isNextPage = false)
                }
                return true
            }
        })
    }

    private fun applyPrefillQuery() {
        val query = prefillQuery?.trim().orEmpty()
        if (query.isBlank()) return
        searchQuery = query
        searchView.setQuery(query, false)
        prefillQuery = null
    }

    private fun fetchPopularWine(isNextPage: Boolean) {
        if (isLoading) return
        if (isNextPage && !shouldEnablePagination()) return
        if (isNextPage && !hasMore) return

        updatePopularTitle()
        wineAdapter.setShowNumberIndicators(!isGeneralSearchMode())

        val requestPage = if (isNextPage) currentPage + 1 else 1
        isLoading = true

        searchRepository.getPopularWineItemsFromServer(
            keyword = buildSearchKeyword(),
            category = mapCategoryToApiCategory(selectedCategory),
            page = requestPage,
            limit = pageSize
        ) { success, fetchedItems, error ->
            requireActivity().runOnUiThread {
                isLoading = false
                if (success) {
                    currentPage = requestPage
                    hasMore = shouldEnablePagination() && fetchedItems.size >= pageSize
                    val nextRankStart = if (isNextPage) wineItems.size + 1 else 1
                    val rankedItems = fetchedItems.mapIndexed { index, item ->
                        item.copy(rank = nextRankStart + index)
                    }

                    if (!isNextPage) {
                        wineItems.clear()
                    }
                    wineItems.addAll(rankedItems)
                    wineAdapter.notifyDataSetChanged()
                } else {
                    Log.e("SearchFragment", "PopularWine 조회 실패: $error")
                    if (!isNextPage) {
                        wineItems.clear()
                        wineItems.addAll(searchRepository.getPopularWineItems())
                        wineAdapter.notifyDataSetChanged()
                        hasMore = false
                        currentPage = 1
                    }
                }
            }
        }
    }

    private fun shouldEnablePagination(): Boolean {
        return selectedCategory != null
    }

    private fun isGeneralSearchMode(): Boolean {
        return selectedCategory == null && searchQuery.isNotBlank()
    }

    private fun updatePopularTitle() {
        tvPopularTitle.text = when {
            isGeneralSearchMode() -> "검색 결과"
            selectedCategory != null -> selectedCategory?.title.orEmpty()
            else -> "인기 검색 와인"
        }
    }

    private fun buildSearchKeyword(): String? {
        return searchQuery.trim().ifBlank { null }
    }

    private fun mapCategoryToApiCategory(category: SearchCategoryItem?): String? {
        val raw = category?.title.orEmpty()
        return when {
            raw.contains("레드") -> "Red Wine"
            raw.contains("화이트") -> "White Wine"
            raw.contains("로제") -> "Rosé Wine"
            raw.contains("스파클링") -> "Sparkling Wine & Champagne"
            else -> null
        }
    }
}
