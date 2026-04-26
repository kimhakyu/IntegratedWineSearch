package com.example.integratedwinesearch.chat.model

data class ChatMessage(
    val id: String,
    val message: String,
    val time: String,
    val isUser: Boolean
)