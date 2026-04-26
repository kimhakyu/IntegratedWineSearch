package com.example.integratedwinesearch

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.model.WineItem

class RecentWineRepository {

    fun getRecentWineItems(): List<WineItem> {
        return listOf(
            WineItem(
                id = "201",
                type = "레드",
                grade = "A+",
                name = "샤토 마고 2015",
                price = 850000,
                imageResId = R.drawable.sample_wine_red,
                viewedTime = "2시간 전"
            ),
            WineItem(
                id = "202",
                type = "화이트",
                grade = "B+",
                name = "샤블리 프리미에 크뤼",
                price = 98000,
                imageResId = R.drawable.sample_wine_red,
                viewedTime = "어제"
            ),
            WineItem(
                id = "203",
                type = "스파클링",
                grade = "A-",
                name = "모엣 샹동 브뤼",
                price = 79000,
                imageResId = R.drawable.sample_wine_red,
                viewedTime = "3일 전"
            )
        )
    }
}