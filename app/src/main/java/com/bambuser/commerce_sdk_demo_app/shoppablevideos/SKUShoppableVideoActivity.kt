package com.bambuser.commerce_sdk_demo_app.shoppablevideos

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.bambuser.commerce_sdk_demo_app.HostApplication
import com.bambuser.social_commerce_sdk.data.BambuserCollection
import com.bambuser.social_commerce_sdk.data.BambuserCollectionInfo
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.BambuserVideoAsset
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerConfiguration
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerDelegate
import com.bambuser.social_commerce_sdk.data.ViewActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SKUShoppableVideoActivity : ComponentActivity() {
    val tag = "SKUShoppableVideoActivity"

    val bambuserCollectionStateFlow = MutableStateFlow<BambuserCollection?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as HostApplication

        enableEdgeToEdge()

        lifecycleScope.launch {

            try {
                val response = application.globalBambuserSDK.getShoppableVideoPlayerCollection(
                    BambuserCollectionInfo.SKU(
                        orgId = "BdTubpTeJwzvYHljZiy4",
                        sku = "b7c5", // product SKU
                    ),
                )
                bambuserCollectionStateFlow.value = response
            } catch (e: Exception) {
                Log.d(tag, "getShoppableVideoPlayerCollection has exception: $e")
            }
        }

        setContent {

            val collection = bambuserCollectionStateFlow.collectAsState()

            collection.value?.let { collection ->
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp
                val screenHeight = configuration.screenHeightDp.dp

                val itemWidth = screenWidth / 2
                val itemHeight = screenHeight / 2

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(collection.videoIdList.size) { item ->
                        Box(
                            modifier = Modifier
                                .size(itemWidth, itemHeight)
                                .background(color = Color.White)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            application.globalBambuserSDK.GetLShoppableVideoView(
                                videoConfiguration = BambuserVideoPlayerConfiguration(
                                    events = listOf("*"),
                                    configuration = mapOf(
                                        "thumbnail" to mapOf(
                                            "enabled" to true,
                                            "showPlayButton" to true,
                                            "showLoadingIndicator" to true,
//                                            "preview" to "", Add a custom thumbnail image URL if needed
                                        ),
                                        "previewConfig" to mapOf(
                                            "settings" to "products:true; title: false; actions:1; productCardMode: thumbnail",
                                        ),
                                        "playerConfig" to mapOf(
                                            "buttons" to mapOf(
                                                "dismiss" to "event",
                                                "product" to "event"
                                            ),
                                            "currency" to "USD", // Mandatory for product hydration
                                        )
                                    ),
                                    videoType = BambuserVideoAsset.Shoppable(collection.videoIdList[item]),
                                ),
                                videoPlayerDelegate = object : BambuserVideoPlayerDelegate {
                                    override fun onNewEventReceived(
                                        playerId: String,
                                        event: BambuserEventPayload,
                                        viewAction: ViewActions,
                                    ) {
                                        Log.d(tag, "onNewEventReceived: $event")
                                    }

                                    override fun onErrorOccurred(
                                        playerId: String,
                                        error: Exception,
                                    ) {
                                        Log.d(tag, "onErrorOccurred: $error")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}