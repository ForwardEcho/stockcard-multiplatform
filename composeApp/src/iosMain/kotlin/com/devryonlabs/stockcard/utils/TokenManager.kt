package com.devryonlabs.stockcard.utils

import platform.Foundation.NSUserDefaults

actual object TokenManager {
    private val delegate = NSUserDefaults.standardUserDefaults

    actual fun saveToken(token: String) {
        delegate.setObject(token, "jwt_token")
    }

    actual fun getToken(): String? {
        return delegate.stringForKey("jwt_token")
    }

    actual fun saveUserName(name: String) {
        delegate.setObject(name, "USER_NAME")
    }

    actual fun getUserName(): String {
        return delegate.stringForKey("USER_NAME") ?: "User"
    }

    actual fun clearToken() {
        delegate.removeObjectForKey("jwt_token")
    }
}