package com.example.integratedwinesearch


import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.WineItem

class RecommendRepository {

    fun getRecommendItems(): List<WineItem> {
        return listOf(
            WineItem(
                id = "101",
                type = "레드",
                grade = "A-",
                name = "카베르네 소비뇽 나파",
                price = 135000,
                imageResId = R.drawable.sample_wine_red,
                description = "당신의 취향 기반 추천"
            ),
            WineItem(
                id = "101",
                type = "레드",
                grade = "A-",
                name = "카베르네 소비뇽 나파",
                price = 135000,
                imageResId = R.drawable.sample_wine_red,
                description = "당신의 취향 기반 추천"
            ),
            WineItem(
                id = "101",
                type = "레드",
                grade = "A-",
                name = "카베르네 소비뇽 나파",
                price = 135000,
                imageResId = R.drawable.sample_wine_red,
                description = "당신의 취향 기반 추천"
            ),
            WineItem(
                id = "101",
                type = "레드",
                grade = "A-",
                name = "카베르네 소비뇽 나파",
                price = 135000,
                imageResId = R.drawable.sample_wine_red,
                description = "당신의 취향 기반 추천"
            ),
        )
    }
}