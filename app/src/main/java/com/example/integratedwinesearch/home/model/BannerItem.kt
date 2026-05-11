package com.example.integratedwinesearch.home.model

data class BannerItem(
    val id: String,
    val imageResId: Int,
    val imageUrl: String? = null,
    val title: String,
    val subTitle: String
)
