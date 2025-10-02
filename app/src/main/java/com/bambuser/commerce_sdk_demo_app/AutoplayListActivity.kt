package com.bambuser.commerce_sdk_demo_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AutoplayListActivity : ComponentActivity() {

    private val tag = "HorizontalFeed"
    private val bambuserCollectionStateFlow = MutableStateFlow<BambuserCollection?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = application as HostApplication
        enableEdgeToEdge()

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
                HorizontalVideoFeed(
                    collection = it,
                    bambuserSDK = application.globalBambuserSDK
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalVideoFeed(
    collection: BambuserCollection,
    bambuserSDK: BambuserSDK,
) {
    val listState = rememberLazyListState()

    val mostVisibleIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf -1

            val viewportCentre = layoutInfo.viewportEndOffset / 2
            visibleItems.minByOrNull {
                val itemCentre = it.offset + it.size / 2
                kotlin.math.abs(itemCentre - viewportCentre)
            }?.index ?: -1
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .padding(top = 32.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
    ) {
        items(
            count = collection.videoIdList.size,
            key = { index -> collection.videoIdList[index] },
        ) { index ->
            val isPlaying = index == mostVisibleIndex

            var playerActions by remember { mutableStateOf<PlayerActions?>(null) }

            LaunchedEffect(playerActions, isPlaying) {
                if (isPlaying) {
                    playerActions?.play()
                } else {
                    playerActions?.pause()
                }
            }

            Box(
                modifier = Modifier
                    .aspectRatio(9f / 20f)
                    .background(color = Color.White),
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
                            }

                            override fun onErrorOccurred(playerId: String, error: Exception) {}

                            override fun onVideoStatusChanged(
                                playerId: String,
                                state: BambuserVideoState,
                                actions: PlayerActions
                            ) {
                                playerActions = actions
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
                    "enabled" to true,
                    "showPlayButton" to true,
                    "showLoadingIndicator" to true,
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