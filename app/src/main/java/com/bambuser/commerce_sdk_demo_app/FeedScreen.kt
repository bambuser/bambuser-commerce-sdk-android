package com.bambuser.commerce_sdk_demo_app

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bambuser.commerce_sdk_demo_app.feed.FeedViewModel
import com.bambuser.commerce_sdk_demo_app.feed.FeedViewModel.NavigationEvent
import com.bambuser.commerce_sdk_demo_app.ui.UiState
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.BambuserVideoAsset
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerConfiguration
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerDelegate
import com.bambuser.social_commerce_sdk.data.BambuserVideoState
import com.bambuser.social_commerce_sdk.data.PlayerActions
import com.bambuser.social_commerce_sdk.data.ScreenMode
import com.bambuser.social_commerce_sdk.data.ViewActions

private const val TAG = "FeedScreen"

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(),
    onNavigateToWishlist: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                NavigationEvent.OpenWishlist -> onNavigateToWishlist()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.load() }) {
                        Text("Retry")
                    }
                }
            }

            is UiState.Content -> {
                val videoIds = state.data
                if (videoIds.isEmpty()) {
                    Text(
                        text = "No videos\nCheck back soon.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    FeedPager(videoIds = videoIds, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun FeedPager(videoIds: List<String>, viewModel: FeedViewModel) {
    val application = LocalContext.current.applicationContext as HostApplication
    val sdkInstance = application.globalBambuserSDK

    val pagerState = rememberPagerState(pageCount = { videoIds.size })
    val isModalOpen by viewModel.isModalOpen.collectAsState()

    val videoPlayerDelegate = remember {
        object : BambuserVideoPlayerDelegate {

            override fun onNewEventReceived(
                playerId: String,
                event: BambuserEventPayload,
                viewAction: ViewActions,
            ) {
                val type = event.event
                when(type) {
                    "preview-should-expand" -> {
                        viewAction.switchScreenMode(ScreenMode.FullScreenMode)
                    }
                    "close" -> {
                        viewAction.switchScreenMode(ScreenMode.PreviewMode)
                    }
                }
                viewModel.handleEvent(event, viewAction)
            }

            override fun onErrorOccurred(
                playerId: String,
                error: Exception,
            ) {
                Log.d(TAG, "onErrorOccurred [$playerId]: $error")
            }

            override fun onVideoStatusChanged(
                playerId: String,
                state: BambuserVideoState,
                playerActions: PlayerActions,
            ) {
                Log.d(TAG, "onVideoStatusChanged [$playerId]: $state")
            }

            override fun onVideoProgress(
                playerId: String,
                duration: Long,
                currentTime: Long,
            ) {
                Log.d(TAG, "onVideoProgress [$playerId]: $currentTime / $duration")
            }
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        // While the product modal is open, let the WebView handle its own scrolling
        // instead of swiping the pager to the next video.
        userScrollEnabled = !isModalOpen,
    ) { page ->
        sdkInstance.GetLShoppableVideoView(
            videoConfiguration = BambuserVideoPlayerConfiguration(
                events = listOf("*"),
                configuration = mapOf(
                    "preload" to true,
                    "thumbnail" to mapOf(
                        "enabled" to true,
                        "showPlayButton" to true,
                        "contentMode" to "scaleAspectFill",
                        "showLoadingIndicator" to true,
                    ),
                    "previewConfig" to mapOf(
                        "productAction" to "modal",
                        "closedCaptions" to "original",
                        "settings" to "products:true; title:false; actions:true; productCardMode: thumbnail; autoplay:true",
                    ),
                    "playerConfig" to mapOf(
                        "buttons" to mapOf("dismiss" to "event"),
                        "enableTrackingPoint" to false,
                        "currency" to "SEK",
                        "locale" to "en-US",
                    ),
                ),
                videoType = BambuserVideoAsset.Shoppable(videoIds[page]),
            ),
            videoPlayerDelegate = videoPlayerDelegate,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedScreenPreview() {
    CommerceSDKDemoAppTheme {
        FeedScreen()
    }
}
