package com.bambuser.commerce_sdk_demo_app.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bambuser.commerce_sdk_demo_app.HostApplication
import com.bambuser.commerce_sdk_demo_app.data.ProductHydrationDataSource
import com.bambuser.commerce_sdk_demo_app.data.Storage
import com.bambuser.commerce_sdk_demo_app.ui.UiState
import com.bambuser.social_commerce_sdk.data.BambuserCollectionInfo
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.ViewActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val sdkInstance = (application as HostApplication).globalBambuserSDK

    private val _uiState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<String>>> = _uiState

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents

    /**
     * Whether the in-player product modal is currently open. While it is, the feed pager should
     * stop intercepting vertical drags so the modal's own content can scroll.
     */
    private val _isModalOpen = MutableStateFlow(false)
    val isModalOpen: StateFlow<Boolean> = _isModalOpen

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val collection = sdkInstance.getShoppableVideoPlayerCollection(
                    BambuserCollectionInfo.Playlist(
                        componentId = COMPONENT_ID,
                        orgId = ORG_ID,
                    )
                )
                _uiState.value = UiState.Content(collection.videoIdList)
            } catch (e: Exception) {
                Log.d(TAG, "Failed to load feed: $e")
                _uiState.value = UiState.Error(e.message ?: "Failed to load feed")
            }
        }
    }

    fun handleEvent(event: BambuserEventPayload, viewActions: ViewActions) {
        Log.d(TAG, "onNewEventReceived: $event")

        when (event.event) {
            "should-show-product-view" -> {
                _isModalOpen.value = true
            }

            "should-hide-product-view" -> {
                _isModalOpen.value = false
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
                viewModelScope.launch {
                    _navigationEvents.emit(NavigationEvent.OpenWishlist)
                }
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
        data object OpenWishlist : NavigationEvent
    }

    companion object {
        private const val TAG = "FeedViewModel"
        private const val ORG_ID = "BdTubpTeJwzvYHljZiy4"
        private const val COMPONENT_ID = "mobile-sdk-tests"
    }
}
