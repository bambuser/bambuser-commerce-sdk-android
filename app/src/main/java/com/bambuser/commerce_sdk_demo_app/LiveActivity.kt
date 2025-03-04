package com.bambuser.commerce_sdk_demo_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.BambuserVideoAsset
import com.bambuser.social_commerce_sdk.data.BambuserVideoConfiguration
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerDelegate
import com.bambuser.social_commerce_sdk.ui.components.live.LiveView

class LiveActivity : ComponentActivity() {

    val tag = "LiveActivity"
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = intent.getStringExtra("eventId")
        setContent {
            CommerceSDKDemoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    eventId?.let { id ->
                        // This is our composable view to show the player
                        LiveView(
                            modifier = Modifier.padding(innerPadding),
                            // This is the configuration for the player
                            videoConfiguration = BambuserVideoConfiguration(
                                // Pass list of events you want to receive
                                // Or "*" to receive all events
                                events = listOf("*"),
                                // Pass the configuration for the video player
                                // You can find more useful configurations in the documentation
                                // https://bambuser.com/docs/live/player-api-reference/#constants
                                configuration = mapOf(
                                    "buttons" to mapOf(
                                        "dismiss" to "none",
                                    ),
                                ),
                                // Pass the asset you want to play, we support only Live for now
                                videoType = BambuserVideoAsset.Live(id),
                            ),

                            // Create a video player delegate to receive events and errors
                            videoPlayerDelegate = object : BambuserVideoPlayerDelegate {

                                override fun onNewEventReceived(
                                    playerId: String,
                                    event: BambuserEventPayload,
                                ) {
                                    // Add your logic here for receiving events
                                    Log.d(tag, "onNewEventReceived: $event, playerId: $playerId")
                                }

                                override fun onErrorOccurred(
                                    playerId: String,
                                    error: Exception,
                                ) {
                                    // Add your logic here for receiving errors
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