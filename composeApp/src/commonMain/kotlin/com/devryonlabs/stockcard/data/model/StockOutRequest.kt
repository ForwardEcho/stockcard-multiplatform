package com.devryonlabs.stockcard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StockOutRequest(
    val id: String,
    val quantity: Int
)