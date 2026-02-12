package com.devryonlabs.stockcard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StockItem(
    val id: String,
    val name: String,
    val category: String,
    val stock: Int
)