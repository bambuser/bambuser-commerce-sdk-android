package com.bambuser.commerce_sdk_demo_app.experiences

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.bambuser.commerce_sdk_demo_app.HostApplication
import com.bambuser.social_commerce_sdk.BambuserSDK
import com.bambuser.social_commerce_sdk.data.BambuserCollection
import com.bambuser.social_commerce_sdk.data.BambuserCollectionInfo
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.BambuserVideoAsset
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerConfiguration
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerDelegate
import com.bambuser.social_commerce_sdk.data.BambuserVideoState
import com.bambuser.social_commerce_sdk.data.PlayerActions
import com.bambuser.social_commerce_sdk.data.ViewActions
import com.bambuser.social_commerce_sdk.data.ScreenMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

class VerticalFeedActivity : ComponentActivity() {

    private val tag = "VerticalFeed"
    private val bambuserCollectionStateFlow = MutableStateFlow<BambuserCollection?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = application as HostApplication
        // Enables drawing behind the system bars
        enableEdgeToEdge()

        // Fetch video data asynchronously using the Activity's lifecycleScope
        lifecycleScope.launch {
            try {
                val response = application.globalBambuserSDK.getShoppableVideoPlayerCollection(
                    BambuserCollectionInfo.Playlist(
                        containerId = "best-sellers",
                        pageId = "mobile-home",
                        orgId = "BdTubpTeJwzvYHljZiy4",
                        packageName = "com.bambuser.commerce.sdk.demo",
                    ),
                )
                bambuserCollectionStateFlow.value = response
            } catch (e: Exception) {
                Log.e(tag, "getShoppableVideoPlayerCollection has exception: $e")
            }
        }

        setContent {
            val collection by bambuserCollectionStateFlow.collectAsState()
            collection?.let {
                VerticalVideoFeed(
                    collection = it,
                    bambuserSDK = application.globalBambuserSDK
                )
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Optional: Show a loading indicator while data is fetched
                // CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalVideoFeed(
    collection: BambuserCollection,
    bambuserSDK: BambuserSDK,
) {
    val listState = rememberLazyListState()

    // Derived state to track which item is currently centered in the viewport
    val mostVisibleIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf -1

            val viewportCentre = layoutInfo.viewportEndOffset / 2

            // Find the item whose center is closest to the viewport's center
            visibleItems.minByOrNull {
                val itemCentre = it.offset + it.size / 2
                abs(itemCentre - viewportCentre)
            }?.index ?: -1
        }
    }

    // LazyColumn setup for vertical, full-screen, snap-scrolling
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        // Enable snapping behavior for TikTok-style page-by-page scrolling
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
    ) {
        items(
            count = collection.videoIdList.size,
            key = { index -> collection.videoIdList[index] },
        ) { index ->
            val isPlaying = index == mostVisibleIndex

            var playerActions by remember { mutableStateOf<PlayerActions?>(null) }
            var viewActions by remember { mutableStateOf<ViewActions?>(null) }

            // Autoplay/Pause logic triggered by visibility
            LaunchedEffect(playerActions, isPlaying) {
                if (isPlaying) {
                    playerActions?.play()
                } else {
                    playerActions?.pause()
                }
            }

            Box(
                // fillParentMaxSize() ensures this item takes up the entire viewport (screen)
                modifier = Modifier
                    .fillParentMaxSize()
                    .background(color = Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                bambuserSDK.GetLShoppableVideoView(
                    videoConfiguration = createVideoConfig(collection.videoIdList[index]),
                    videoPlayerDelegate = remember {
                        object : BambuserVideoPlayerDelegate {
                            override fun onNewEventReceived(
                                playerId: String,
                                event: BambuserEventPayload,
                                viewAction: ViewActions
                            ) {
                                viewActions = viewAction // Store ViewActions
                            }

                            override fun onErrorOccurred(playerId: String, error: Exception) {
                                Log.e("tag", "Player Error: $error")
                            }

                            override fun onVideoStatusChanged(
                                playerId: String,
                                state: BambuserVideoState,
                                actions: PlayerActions
                            ) {
                                playerActions = actions

                                // 🎯 Logic to go FullScreen and Autoplay after Initialization
                                // We use 'Ready' as the initialization signal.
                                if (state == BambuserVideoState.INITIALIZED || state == BambuserVideoState.PLAYING) {
                                    // 1. Switch to FullScreen Mode
                                    viewActions?.switchScreenMode(ScreenMode.FullScreenMode)

                                    // 2. Play if the video is currently the most visible one
                                    if (isPlaying) {
                                        actions.play()
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun createVideoConfig(videoId: String): BambuserVideoPlayerConfiguration {
    return remember(videoId) {
        BambuserVideoPlayerConfiguration(
            events = listOf("*"),
            videoType = BambuserVideoAsset.Shoppable(videoId),
            configuration = mapOf(
                "thumbnail" to mapOf(
                    "enabled" to false, // Disabled thumbnail for seamless autoplay experience
                ),
                "previewConfig" to mapOf(
                    "settings" to "products:true; title: false; actions:1; productCardMode: thumbnail",
                ),
                "playerConfig" to mapOf(
                    "buttons" to mapOf(
                        "dismiss" to "event",
                        "product" to "event"
                    ),
                    "currency" to "USD",
                )
            )
        )
    }
}