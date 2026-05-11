package com.example.integratedwinesearch.model

data class CategoryItem(
    val id: String,
    val name: String,
    val imageResId: Int,
    val imageUrl: String? = null
)
