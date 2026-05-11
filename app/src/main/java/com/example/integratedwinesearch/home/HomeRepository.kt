package com.example.integratedwinesearch.home

import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.home.model.BannerItem

class HomeRepository {

    fun getBannerItems(): List<BannerItem> {
        return listOf(
            BannerItem(
                id = "1",
                imageResId = R.drawable.sample_banner_red,
                imageUrl = "https://images.pexels.com/photos/2912108/pexels-photo-2912108.jpeg?auto=compress&cs=tinysrgb&w=1600",
                title = "Wine Festival",
                subTitle = "와인 페스티벌 2026"
            ),
            BannerItem(
                id = "2",
                imageResId = R.drawable.sample_banner_dark,
                imageUrl = "https://images.pexels.com/photos/1407846/pexels-photo-1407846.jpeg?auto=compress&cs=tinysrgb&w=1600",
                title = "Premium Wine",
                subTitle = "프리미엄 와인 컬렉션"
            ),
            BannerItem(
                id = "3",
                imageResId = R.drawable.sample_banner_gold,
                imageUrl = "https://images.pexels.com/photos/1123260/pexels-photo-1123260.jpeg?auto=compress&cs=tinysrgb&w=1600",
                title = "Special Event",
                subTitle = "시즌 한정 특별 할인"
            )
        )
    }
}
