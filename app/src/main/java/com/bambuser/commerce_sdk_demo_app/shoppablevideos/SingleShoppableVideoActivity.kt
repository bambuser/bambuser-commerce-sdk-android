package com.bambuser.commerce_sdk_demo_app.shoppablevideos

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bambuser.commerce_sdk_demo_app.HostApplication
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.BambuserVideoAsset
import com.bambuser.social_commerce_sdk.data.BambuserVideoConfiguration
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerDelegate
import com.bambuser.social_commerce_sdk.data.ScreenMode
import com.bambuser.social_commerce_sdk.data.ViewActions
import kotlinx.coroutines.launch

class SingleShoppableVideoActivity : ComponentActivity() {

    val tag = "SingleShoppableVideoActivity"
    private var videoId = "puv_sxSLL9s5K16wDNZNuqVjvk"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val application = application as HostApplication

        enableEdgeToEdge()

        setContent {
            CommerceSDKDemoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        application.globalBambuserSDK.GetLShoppableVideoView(
                            modifier = Modifier.fillMaxSize(),
                            videoConfiguration = BambuserVideoConfiguration(
                                events = listOf("*"),
                                videoType = BambuserVideoAsset.Shoppable(videoId),
                                configuration = mapOf(
                                    "thumbnail" to mapOf(
                                        "enabled" to true, // IF false, it will show a black screen
                                        "showPlayButton" to true,
                                        "showLoadingIndicator" to true,
//                                            "preview" to "", Add a custom thumbnail image URL if needed
                                    ),
                                    // Configuration for preview mode
                                    "previewConfig" to mapOf(
                                        "settings" to "products:true; title: false; actions:1; productCardMode: thumbnail",
                                    ),
                                    // Configuration for fullscreen mode
                                    "playerConfig" to mapOf(
                                        "buttons" to mapOf(
                                            "dismiss" to "event",
                                            "product" to "event",
                                        ),
                                        "currency" to "USD", // Mandatory for product hydration
                                    )
                                ),
                            ),
                            videoPlayerDelegate = object : BambuserVideoPlayerDelegate {
                                override fun onNewEventReceived(
                                    playerId: String,
                                    event: BambuserEventPayload,
                                    viewAction: ViewActions
                                ) {
                                    lifecycleScope.launch {
                                        Log.d(tag, "onNewEventReceived: $event")
                                        val type = event.event
                                        when (type) {
                                            "preview-should-expand" -> {
                                                viewAction.switchScreenMode(ScreenMode.FullScreenMode)
                                            }

                                            "close" -> {
                                                viewAction.switchScreenMode(ScreenMode.PreviewMode)
                                            }
                                        }
                                    }
                                }

                                override fun onErrorOccurred(
                                    playerId: String,
                                    error: Exception
                                ) {
                                    Log.d(
                                        tag,
                                        "onErrorOccurred: playerId: $playerId , error: $error"
                                    )
                                }
                            },
                            piPState = null,
                        )
                    }
                }
            }
        }
    }
}