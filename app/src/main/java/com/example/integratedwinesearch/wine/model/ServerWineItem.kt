package com.example.integratedwinesearch.wine.model

data class ServerWineItem(
    val id: Int,
    val name: String,
    val nameKr: String? = null,
    val area: String,
    val category: String,
    val price: Int,
    val imageUrl: String,
    val description: String? = null,
    val viewCount: Int = 0
) {
    val displayName: String
        get() = nameKr?.takeIf { it.isNotBlank() } ?: name
}
