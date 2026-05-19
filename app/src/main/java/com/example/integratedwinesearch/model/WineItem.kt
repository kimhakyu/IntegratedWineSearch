package com.example.integratedwinesearch.model

data class WineItem(
    val id: String,
    val type: String,
    val region: String = "",
    val grade: String,
    val name: String,
    val price: Int,
    val imageResId: Int,
    val imageUrl: String? = null,

    // 선택적 데이터 (필요할 때만 사용)
    val description: String? = null,   // 추천용
    val viewedTime: String? = null     // 최근 본 와인용
)
