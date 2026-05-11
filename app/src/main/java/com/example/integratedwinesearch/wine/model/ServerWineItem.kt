package com.example.integratedwinesearch.wine.model

data class ServerWineItem(
    val id: Int,
    val name: String,
    val area: String,
    val category: String,
    val price: Int,
    val imageUrl: String
)