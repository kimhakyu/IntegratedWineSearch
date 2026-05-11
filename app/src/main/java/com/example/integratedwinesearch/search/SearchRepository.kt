package com.example.integratedwinesearch.search

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.search.model.SearchCategoryItem
import com.example.integratedwinesearch.search.model.SearchWineItem
import com.example.integratedwinesearch.wine.WineRepository

class SearchRepository {

    private val wineRepository = WineRepository()

    fun getSearchCategoryItems(): List<SearchCategoryItem> {
        return listOf(
            SearchCategoryItem(
                id = "1",
                title = "레드 와인",
                imageResId = R.drawable.sample_wine_red,
                overlayColor = "#66A1161D",
                imageUrl = "https://images.pexels.com/photos/7616477/pexels-photo-7616477.jpeg?auto=compress&cs=tinysrgb&w=600"
            ),
            SearchCategoryItem(
                id = "2",
                title = "화이트 와인",
                imageResId = R.drawable.sample_wine_red,
                overlayColor = "#668A5A00",
                imageUrl = "https://images.pexels.com/photos/10403547/pexels-photo-10403547.jpeg?auto=compress&cs=tinysrgb&w=600"
            ),
            SearchCategoryItem(
                id = "3",
                title = "로제 와인",
                imageResId = R.drawable.sample_wine_red,
                overlayColor = "#667F2E4C",
                imageUrl = "https://images.pexels.com/photos/7283382/pexels-photo-7283382.jpeg?auto=compress&cs=tinysrgb&w=600"
            ),
            SearchCategoryItem(
                id = "4",
                title = "스파클링",
                imageResId = R.drawable.sample_wine_red,
                overlayColor = "#66476C9B",
                imageUrl = "https://images.pexels.com/photos/1407846/pexels-photo-1407846.jpeg?auto=compress&cs=tinysrgb&w=600"
            )
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

    fun getPopularWineItemsFromServer(
        keyword: String? = null,
        page: Int = 1,
        limit: Int = 10,
        callback: (Boolean, List<SearchWineItem>, String?) -> Unit
    ) {
        wineRepository.getWines(
            search = keyword,
            page = page,
            limit = limit
        ) { success, wines, error ->
            if (!success) {
                callback(false, emptyList(), error)
                return@getWines
            }

            val mapped = wines.mapIndexed { index, wine ->
                SearchWineItem(
                    id = wine.id.toString(),
                    rank = index + 1,
                    name = wine.name,
                    type = wine.category,
                    region = wine.area,
                    price = wine.price,
                    searchCount = 0,
                    grade = "A",
                    imageResId = R.drawable.sample_wine_red,
                    imageUrl = normalizeImageUrl(wine.imageUrl)
                )
            }

            callback(true, mapped, null)
        }
    }

    private fun normalizeImageUrl(imageUrl: String?): String? {
        if (imageUrl.isNullOrBlank()) return null
        return if (imageUrl.startsWith("None/")) {
            "http://15.164.220.246:8000/" + imageUrl.replace("None/", "images/")
        } else {
            imageUrl
        }
    }
}
