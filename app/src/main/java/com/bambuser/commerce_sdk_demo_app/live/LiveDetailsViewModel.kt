package com.bambuser.commerce_sdk_demo_app.live

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bambuser.commerce_sdk_demo_app.data.ProductHydrationDataSource
import com.bambuser.commerce_sdk_demo_app.data.Storage
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.ViewActions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LiveDetailsViewModel : ViewModel() {

    private val _navigationEvents = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    fun handleEvent(event: BambuserEventPayload, viewActions: ViewActions) {
        Log.d(TAG, "onNewEventReceived: $event")

        when (event.event) {
            "close" -> {
                viewModelScope.launch { _navigationEvents.send(NavigationEvent.GoBack) }
                return
            }

            "provide-product-data" -> {
                (event.data["products"] as? List<Map<String, Any>>)?.let { products ->
                    products.forEach { product ->
                        val ref = product["ref"]?.toString() ?: return@forEach
                        val id = product["id"]?.toString() ?: return@forEach
                        val hydratedProduct = ProductHydrationDataSource.findHydratedProduct(ref) ?: return@forEach
                        val arguments = "'$id', ${hydratedProduct.toJsonObjectString()}"
                        viewModelScope.launch {
                            viewActions.invoke(
                                function = "updateProductWithData",
                                arguments = arguments,
                            )
                        }
                    }
                }
            }

            "should-add-item-to-cart", "should-update-item-in-cart" -> {
                val eventMap = event.data
                val sku = eventMap["sku"]?.toString() ?: return
                val quantity = (eventMap["quantity"] as? Number)?.toInt() ?: 1

                viewModelScope.launch {
                    delay(2000.milliseconds)
                    event.callbackKey?.let { callbackKey ->
                        if (quantity > 3) {
                            viewActions.notifyView(
                                callbackKey = callbackKey,
                                info = "{success: false, reason: 'out-of-stock'}",
                            )
                        } else {
                            if (event.event == "should-add-item-to-cart") {
                                Storage.addToCart(sku, quantity)
                            } else {
                                Storage.setCartQuantity(sku, quantity)
                            }
                            viewActions.notifyView(callbackKey = callbackKey, info = true)
                        }
                    }
                }
            }

            "add-to-wishlist" -> {
                val sku = event.data["sku"]?.toString() ?: return
                Storage.addToWishlist(sku)

                viewModelScope.launch {
                    delay(2000.milliseconds)
                    event.callbackKey?.let { callbackKey ->
                        viewActions.notifyView(
                            callbackKey = callbackKey,
                            info = "{success: true, sku:'$sku'}",
                        )
                    }
                }
            }

            "remove-from-wishlist" -> {
                val sku = event.data["sku"]?.toString() ?: return
                Storage.removeFromWishlist(sku)

                event.callbackKey?.let { callbackKey ->
                    viewModelScope.launch {
                        viewActions.notifyView(
                            callbackKey = callbackKey,
                            info = "{success: true, sku:'$sku'}",
                        )
                    }
                }
            }

            "open-wishlist" -> {
                viewModelScope.launch { _navigationEvents.send(NavigationEvent.OpenWishlist) }
            }

            "provide-wishlist-status" -> {
                val json = Storage.wishlist.value.entries.joinToString(",", "{\"statuses\":{", "}}") { (k, _) ->
                    "\"$k\":true"
                }
                viewModelScope.launch {
                    viewActions.invoke(function = "updateWishlistStatus", arguments = json)
                }
            }
        }
    }

    sealed interface NavigationEvent {
        data object GoBack : NavigationEvent
        data object OpenWishlist : NavigationEvent
    }

    companion object {
        private const val TAG = "LiveDetailsViewModel"
    }
}
