package com.example.integratedwinesearch

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.CategoryItem


class CategoryRepository {
    fun getCategoryItems(): List<CategoryItem> {
        return listOf(
            CategoryItem("1", "레드", R.drawable.sample_category_red),
            CategoryItem("2", "화이트", R.drawable.sample_category_white),
            CategoryItem("3", "로제", R.drawable.sample_category_rose),
            CategoryItem("4", "스파클링", R.drawable.sample_category_sparkling),
            CategoryItem("4", "스파클링", R.drawable.sample_category_sparkling),
                    CategoryItem("4", "스파클링", R.drawable.sample_category_sparkling)
        )
    }
}