package com.example.integratedwinesearch.search.model

data class SearchWineItem(
    val id: String,
    val rank: Int,
    val name: String,
    val type: String,
    val region: String,
    val price: Int,
    val searchCount: Int,
    val grade: String,
    val imageResId: Int,
    val imageUrl: String? = null
)
