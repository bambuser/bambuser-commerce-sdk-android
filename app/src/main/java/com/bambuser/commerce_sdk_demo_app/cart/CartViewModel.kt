package com.bambuser.commerce_sdk_demo_app.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bambuser.commerce_sdk_demo_app.data.HydratedProduct
import com.bambuser.commerce_sdk_demo_app.data.ProductHydrationDataSource
import com.bambuser.commerce_sdk_demo_app.data.Storage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CartItem(
    val id: String,
    val title: String,
    val brand: String,
    val imageUrl: String?,
    val sizeName: String?,
    val unitPrice: Double,
    val original: Double?,
    val currency: String,
    val quantity: Int,
) {
    val lineTotal: Double get() = unitPrice * quantity
}

class CartViewModel : ViewModel() {

    val items: StateFlow<List<CartItem>> = Storage.cart
        .map { cartMap ->
            cartMap
                .filter { it.value > 0 }
                .mapNotNull { (productId, qty) ->
                    val product = ProductHydrationDataSource.findHydratedProduct(productId) ?: return@mapNotNull null
                    val priceInfo = priceInfo(productId, product)

                    CartItem(
                        id = productId,
                        title = product.name,
                        brand = product.brandName,
                        imageUrl = product.variations.firstOrNull()?.imageUrls?.firstOrNull(),
                        sizeName = priceInfo.sizeName,
                        unitPrice = priceInfo.price,
                        original = priceInfo.original,
                        currency = priceInfo.currency,
                        quantity = qty,
                    )
                }
                .sortedWith(compareBy({ it.title }, { it.id }))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subtotal: StateFlow<Double> = items
        .map { list -> list.sumOf { it.lineTotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currency: StateFlow<String> = items
        .map { list -> list.firstOrNull()?.currency ?: "SEK" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SEK")

    fun updateQuantity(productId: String, delta: Int) {
        Storage.addToCart(productId, delta)
    }

    fun remove(productId: String) {
        Storage.removeFromCart(productId)
    }

    fun clearAll() {
        val keys = items.value.map { it.id }
        keys.forEach { Storage.removeFromCart(it) }
    }

    private fun priceInfo(
        fullSku: String,
        product: HydratedProduct,
    ): PriceInfo {
        for (variation in product.variations) {
            for (size in variation.sizes) {
                if (size.sku == fullSku) {
                    return PriceInfo(size.current, size.original, size.currency ?: "SEK", size.name)
                }
            }
        }
        val size = product.variations.firstOrNull()?.sizes?.firstOrNull()
        return PriceInfo(size?.current ?: 0.0, size?.original, size?.currency ?: "SEK", size?.name)
    }

    private data class PriceInfo(
        val price: Double,
        val original: Double?,
        val currency: String,
        val sizeName: String?,
    )
}
