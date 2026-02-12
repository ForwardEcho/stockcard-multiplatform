package com.devryonlabs.stockcard.utils

expect object TokenManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUserName(name: String)
    fun getUserName(): String
    fun clearToken()
}