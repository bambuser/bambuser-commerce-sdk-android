package com.bambuser.commerce_sdk_demo_app.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bambuser.commerce_sdk_demo_app.data.ProductHydrationDataSource
import com.bambuser.commerce_sdk_demo_app.data.Storage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class WishlistItem(
    val sku: String,
    val title: String,
    val brand: String,
    val imageUrl: String?,
    val price: Double,
    val original: Double?,
    val currency: String,
)

class WishlistViewModel : ViewModel() {

    val items: StateFlow<List<WishlistItem>> = Storage.wishlist
        .map { wishlistMap ->
            wishlistMap
                .filter { it.value }
                .keys
                .mapNotNull { sku ->
                    val product = ProductHydrationDataSource.findHydratedProduct(sku) ?: return@mapNotNull null
                    val matchedVariation = product.variations.firstOrNull { it.sku == sku }
                    val variation = matchedVariation ?: product.variations.firstOrNull()
                    val size = variation?.sizes?.firstOrNull()
                    WishlistItem(
                        sku = sku,
                        title = product.name,
                        brand = product.brandName,
                        imageUrl = variation?.imageUrls?.firstOrNull(),
                        price = size?.current ?: 0.0,
                        original = size?.original,
                        currency = size?.currency ?: "SEK",
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun remove(sku: String) {
        Storage.removeFromWishlist(sku)
    }

    fun addToCart(sku: String) {
        Storage.addToCart(sku, 1)
    }
}
