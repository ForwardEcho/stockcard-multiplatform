package com.devryonlabs.stockcard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform