package com.bambuser.commerce_sdk_demo_app.data

import com.google.gson.GsonBuilder

data class HydratedProduct(
    val sku: String,
    val name: String,
    val brandName: String,
    val introduction: String? = null,
    val description: String? = null,
    val variations: List<Variation> = emptyList(),
) {
    fun toJsonObjectString(): String =
        GsonBuilder().setPrettyPrinting().create().toJson(this)
}

data class Variation(
    val sku: String = "",
    val name: String = "",
    val colorName: String = "",
    val imageUrls: List<String> = emptyList(),
    val sizes: List<ProductSize> = emptyList(),
)

data class ProductSize(
    val sku: String = "",
    val current: Double = 0.0,
    val name: String = "",
    val inStock: Int = 0,
    val original: Double? = null,
    val currency: String? = null,
)
