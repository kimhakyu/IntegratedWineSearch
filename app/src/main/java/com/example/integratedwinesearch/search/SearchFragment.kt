package com.example.integratedwinesearch.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.integratedwinesearch.R

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var popularWineRecyclerView: RecyclerView

    private val searchRepository = SearchRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initCategory()
        initPopularWine()
    }

    private fun initViews(view: View) {
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        popularWineRecyclerView = view.findViewById(R.id.popularWineRecyclerView)
    }

    private fun initCategory() {
        val categoryItems = searchRepository.getSearchCategoryItems()

        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.adapter = SearchCategoryAdapter(categoryItems)
    }

    private fun initPopularWine() {
        val wineItems = searchRepository.getPopularWineItems()

        popularWineRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        popularWineRecyclerView.adapter = SearchWineAdapter(wineItems)
    }
}