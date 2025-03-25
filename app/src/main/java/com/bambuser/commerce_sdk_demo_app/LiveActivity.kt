package com.bambuser.commerce_sdk_demo_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.BambuserVideoAsset
import com.bambuser.social_commerce_sdk.data.BambuserVideoConfiguration
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerDelegate
import com.bambuser.social_commerce_sdk.data.ViewActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveActivity : ComponentActivity() {

    val tag = "LiveActivity"
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as HostApplication


        eventId = intent.getStringExtra("eventId")
        setContent {
            CommerceSDKDemoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    eventId?.let { id ->
                        application.globalBambuserSDK.GetLiveView(
                            modifier = Modifier.padding(innerPadding),
                            // This is the configuration for the player
                            videoConfiguration = BambuserVideoConfiguration(
                                //Pass list of events you want to receive
                                // Or "*" to receive all events
                                events = listOf("*"),
                                // Pass the configuration for the video player
                                // You can find more useful configurations in the documentation
                                // https://bambuser.com/docs/live/player-api-reference/#constants
                                configuration = mapOf(
                                    "buttons" to mapOf(
                                        "dismiss" to "none",
                                    ),
                                    "currency" to "USD", // Mandatory for product hydration
                                    "autoplay" to false,
                                ),
                                // Pass the asset you want to play, we support only Live for now
                                videoType = BambuserVideoAsset.Live(id),
                            ),
                            // Create a video player delegate to receive events and errors
                            videoPlayerDelegate = object : BambuserVideoPlayerDelegate {

                                override fun onNewEventReceived(
                                    playerId: String,
                                    event: BambuserEventPayload,
                                    viewActions: ViewActions,
                                ) {
                                    // Add your logic here for receiving events
                                    Log.d(tag, "onNewEventReceived: $event, playerId: $playerId")

                                    when (event.event) {
                                        // If you want to hydrate product
                                        "provide-product-data" -> {
                                            (event.data["products"] as? List<Map<String, Any>>)?.let { products ->
                                                products.forEach { product ->
                                                    product["id"]?.toString()?.let { productId ->
                                                        lifecycleScope.launch {
                                                            // hydrate the product
                                                            viewActions.invoke(
                                                                function = "updateProductWithData",
                                                                arguments = getArguments(productId),
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        // If you want to sync adding product to cart
                                        "should-add-item-to-cart" -> {
                                            lifecycleScope.launch {
                                                // event.data will contain a map of the product
                                                // delay is used to simulate adding item to your cart
                                                delay(2000)
                                                // notify the player that you have added the item to cart successfully
                                                event.callbackKey?.let {
                                                    viewActions.notifyView(
                                                        callbackKey = it,
                                                        info = true,
                                                    )
                                                }
                                            }
                                        }

                                        // If you want to sync updating products in cart
                                        "should-update-item-in-cart" -> {
                                            lifecycleScope.launch {
                                                delay(2000)
                                                if (event.data.containsKey("quantity")) {
                                                    val quantity = event.data["quantity"] as Double
                                                    event.callbackKey?.let {
                                                        if (quantity > 3) {
                                                            // notify the player that the product is out of stock
                                                            viewActions.notifyView(
                                                                callbackKey = it,
                                                                info = "{" +
                                                                        "success: false," +
                                                                        "reason: 'out-of-stock'" +
                                                                        "}",
                                                            )
                                                        } else {
                                                            viewActions.notifyView(
                                                                callbackKey = it,
                                                                info = true,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun onErrorOccurred(
                                    playerId: String,
                                    error: Exception,
                                ) {
                                    // Add your logic here for receiving errors
                                    Log.d(tag, "onErrorOccurred: $error")
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    // An example for what to pass to product hydration function
    fun getArguments(productId: String) = """
           '$productId', {
                  sku: '7777',
                  name: 'Bambuser Hoodie',
                  brandName: 'Bambuser',
                  introduction: 'A nice hoodie that keeps you warm',
                  description: "<div><h2>World's best hoodie</h2><p>Comes in all sizes and forms!</p></div>",
                  variations: [
                    {
                      sku: '1111-black',
                      name: 'Black Bambuser Hoodie',
                      colorName: 'black',
                      imageUrls: [
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-front.png',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-right.jpeg',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-back.jpeg',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-left.jpeg',
                      ],
                      sizes: [
                        {
                          sku: '1111-black-small',
                          currency: 'USD',
                          current: 120,
                          original: 120,
                          name: 'Small',
                          inStock: 9,
                        },
                        {
                          sku: '1111-black-xlarge',
                          currency: 'USD',
                          current: 100,
                          original: 120,
                          name: 'X-Large',
                          inStock: 3,
                        },
                      ],
                    },
                    {
                      sku: '1111-white',
                      name: 'White Bambuser Hoodie',
                      colorName: 'white',
                      images: [
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-front.png',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-right.jpeg',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-back.jpeg',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-left.jpeg',
                      ],
                      sizes: [
                        {
                          sku: '1111-white-small',
                          currency: 'USD',
                          current: 100,
                          original: 120,
                          name: 'Small',
                          inStock: 8,
                        },
                        {
                          sku: '1111-white-xlarge',
                          currency: 'USD',
                          current: 100,
                          original: 120,
                          name: 'X-Large',
                          inStock: 0,
                        },
                      ],
                    },
                    {
                      sku: '1111-white-2',
                      name: '2x White Bambuser Hoodie',
                      colorName: 'white',
                      imagesUrl: [
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-front.png',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-right.jpeg',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-back.jpeg',
                        'https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-left.jpeg',
                      ],
                      sizes: [
                        {
                          sku: '1111-white-small-2',
                          currency: 'USD',
                          current: 180,
                          original: 239,
                          name: 'Small',
                          inStock: 8,
                          perUnit: 90,
                          unitAmount: 1,
                          unitDisplayName: 'st',
                        },
                        {
                          sku: '1111-white-xlarge-2',
                          currency: 'USD',
                          current: 180,
                          original: 239,
                          name: 'X-Large',
                          inStock: 0,
                          perUnit: 90,
                          unitAmount: 1,
                          unitDisplayName: 'st',
                        },
                      ],
                    },
                  ],
                }
        """
}