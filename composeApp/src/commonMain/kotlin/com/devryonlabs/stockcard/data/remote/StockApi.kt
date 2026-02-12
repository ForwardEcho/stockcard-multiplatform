package com.devryonlabs.stockcard.data.remote

import com.devryonlabs.stockcard.data.model.LoginRequest
import com.devryonlabs.stockcard.data.model.LoginResponse
import com.devryonlabs.stockcard.data.model.StockItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class StockApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("https://api-kamu.com/login") {
            setBody(request)
            header("Content-Type", "application/json")
        }.body()
    }

    suspend fun getStocks(token: String): List<StockItem> {
        return client.get("https://api-kamu.com/stocks") {
            header("Authorization", "Bearer $token")
        }.body()
    }
}