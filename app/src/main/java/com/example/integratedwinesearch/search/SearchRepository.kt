package com.example.integratedwinesearch.search

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.search.model.SearchCategoryItem
import com.example.integratedwinesearch.search.model.SearchWineItem

class SearchRepository {

    fun getSearchCategoryItems(): List<SearchCategoryItem> {
        return listOf(
            SearchCategoryItem("1", "레드 와인", R.drawable.sample_wine_red, "#66A1161D"),
            SearchCategoryItem("2", "화이트 와인", R.drawable.sample_wine_red, "#668A5A00"),
            SearchCategoryItem("3", "로제 와인", R.drawable.sample_wine_red, "#667F2E4C"),
            SearchCategoryItem("4", "스파클링", R.drawable.sample_wine_red, "#66476C9B")
        )
    }

    fun getPopularWineItems(): List<SearchWineItem> {
        return listOf(
            SearchWineItem("1", 1, "바롤로 리제르바", "레드 와인", "이탈리아 피에몬테", 180000, 1250, "A", R.drawable.sample_wine_red),
            SearchWineItem("2", 2, "리슬링 슈페트레제", "화이트 와인", "독일 모젤", 75000, 980, "B+", R.drawable.sample_wine_red),
            SearchWineItem("3", 3, "무예 샹동 루제", "스파클링", "프랑스 샴페인", 95000, 875, "A-", R.drawable.sample_wine_red),
            SearchWineItem("4", 4, "피노 누아 말보루", "레드 와인", "뉴질랜드 말보로", 68000, 720, "B+", R.drawable.sample_wine_red),
            SearchWineItem("5", 5, "소비뇽 블랑", "화이트 와인", "뉴질랜드 말보로", 55000, 650, "B+", R.drawable.sample_wine_red)
        )
    }
}