package com.devryonlabs.stockcard.utils

import android.content.Context
import android.content.SharedPreferences

actual object TokenManager {
    private const val PREF_NAME = "stockcard_pref"
    private const val KEY_TOKEN = "jwt_token"
    private lateinit var sharedPreferences: SharedPreferences

    // Fungsi ini khusus di Android untuk inject context dari MainActivity atau StockApp
    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    actual fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    actual fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)

    actual fun saveUserName(name: String) {
        sharedPreferences.edit().putString("USER_NAME", name).apply()
    }

    actual fun getUserName(): String {
        return sharedPreferences.getString("USER_NAME", "User") ?: "User"
    }

    actual fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }
}