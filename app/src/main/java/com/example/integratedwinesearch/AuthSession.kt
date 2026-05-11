package com.example.integratedwinesearch

object AuthSession {
    var isLoggedIn: Boolean = false
    var accessToken: String? = null
    var tokenType: String = "bearer"
    var email: String = ""
    var userName: String = "사용자"

    fun clear() {
        isLoggedIn = false
        accessToken = null
        tokenType = "bearer"
        email = ""
        userName = "사용자"
    }
}