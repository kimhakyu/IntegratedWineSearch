package com.example.integratedwinesearch

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.CategoryItem


class CategoryRepository {
    fun getCategoryItems(): List<CategoryItem> {
        return listOf(
            CategoryItem(
                id = "1",
                name = "레드",
                imageResId = R.drawable.sample_category_red,
                imageUrl = "https://images.pexels.com/photos/7616477/pexels-photo-7616477.jpeg?auto=compress&cs=tinysrgb&w=300"
            ),
            CategoryItem(
                id = "2",
                name = "화이트",
                imageResId = R.drawable.sample_category_white,
                imageUrl = "https://images.pexels.com/photos/10403547/pexels-photo-10403547.jpeg?auto=compress&cs=tinysrgb&w=300"
            ),
            CategoryItem(
                id = "3",
                name = "로제",
                imageResId = R.drawable.sample_category_rose,
                imageUrl = "https://images.pexels.com/photos/7283382/pexels-photo-7283382.jpeg?auto=compress&cs=tinysrgb&w=300"
            ),
            CategoryItem(
                id = "4",
                name = "스파클링",
                imageResId = R.drawable.sample_category_sparkling,
                imageUrl = "https://images.pexels.com/photos/1407846/pexels-photo-1407846.jpeg?auto=compress&cs=tinysrgb&w=300"
            )
        )
    }
}
