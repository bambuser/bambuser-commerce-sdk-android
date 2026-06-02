package com.bambuser.commerce_sdk_demo_app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object Storage {

    private val _wishlist = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val wishlist: StateFlow<Map<String, Boolean>> = _wishlist

    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cart: StateFlow<Map<String, Int>> = _cart

    fun addToWishlist(sku: String) {
        _wishlist.update { it + (sku to true) }
    }

    fun removeFromWishlist(productId: String) {
        _wishlist.update { it - productId }
    }

    fun addToCart(productId: String, amount: Int) {
        _cart.update { current ->
            val newValue = (current[productId] ?: 0) + amount
            if (newValue <= 0) current - productId else current + (productId to newValue)
        }
    }

    fun setCartQuantity(productId: String, total: Int) {
        _cart.update { current ->
            if (total <= 0) current - productId else current + (productId to total)
        }
    }

    fun removeFromCart(productId: String) {
        _cart.update { it - productId }
    }
}
