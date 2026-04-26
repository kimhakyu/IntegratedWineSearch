package com.example.integratedwinesearch.home

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.home.model.BannerItem

class HomeRepository {

    fun getBannerItems(): List<BannerItem> {
        return listOf(
            BannerItem(
                id = "1",
                imageResId = R.drawable.sample_banner_red,
                title = "Wine Festival",
                subTitle = "와인 페스티벌 2026"
            ),
            BannerItem(
                id = "2",
                imageResId = R.drawable.sample_banner_dark,
                title = "Premium Wine",
                subTitle = "프리미엄 와인 컬렉션"
            ),
            BannerItem(
                id = "3",
                imageResId = R.drawable.sample_banner_gold,
                title = "Special Event",
                subTitle = "시즌 한정 특별 할인"
            )
        )
    }
}