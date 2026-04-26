package com.example.integratedwinesearch

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.WineItem

class BestSellerRepository {

    fun getBestSellerItems(): List<WineItem> {
        return listOf(
            WineItem(
                id = "1",
                type = "레드",
                grade = "A",
                name = "바롤로 리제르바",
                price = 180000,
                imageResId = R.drawable.sample_wine_red
            ),
            WineItem(
                id = "2",
                type = "화이트",
                grade = "B+",
                name = "리슬링 슈페트레제",
                price = 75000,
                imageResId = R.drawable.sample_wine_red
            ),
            WineItem(
                id = "2",
                type = "화이트",
                grade = "B+",
                name = "리슬링 슈페트레제",
                price = 75000,
                imageResId = R.drawable.sample_wine_red
            ),
            WineItem(
                id = "2",
                type = "화이트",
                grade = "B+",
                name = "리슬링 슈페트레제",
                price = 75000,
                imageResId = R.drawable.sample_wine_red
            )
        )
    }
}