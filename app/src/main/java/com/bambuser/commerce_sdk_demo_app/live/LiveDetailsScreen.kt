package com.bambuser.commerce_sdk_demo_app.live

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bambuser.commerce_sdk_demo_app.HostApplication
import com.bambuser.social_commerce_sdk.data.BambuserEventPayload
import com.bambuser.social_commerce_sdk.data.BambuserVideoAsset
import com.bambuser.social_commerce_sdk.data.BambuserVideoConfiguration
import com.bambuser.social_commerce_sdk.data.BambuserVideoPlayerDelegate
import com.bambuser.social_commerce_sdk.data.BambuserVideoState
import com.bambuser.social_commerce_sdk.data.PlayerActions
import com.bambuser.social_commerce_sdk.data.ViewActions

private const val TAG = "LiveDetailsScreen"

@Composable
fun LiveDetailsScreen(
    showId: String,
    modifier: Modifier = Modifier,
    viewModel: LiveDetailsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
) {
    val application = LocalContext.current.applicationContext as HostApplication
    val sdkInstance = application.globalBambuserSDK

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                LiveDetailsViewModel.NavigationEvent.GoBack -> onBack()
                LiveDetailsViewModel.NavigationEvent.OpenWishlist -> onNavigateToWishlist()
            }
        }
    }

    val videoPlayerDelegate = remember {
        object : BambuserVideoPlayerDelegate {

            override fun onNewEventReceived(
                playerId: String,
                event: BambuserEventPayload,
                viewAction: ViewActions,
            ) {
                viewModel.handleEvent(event, viewAction)
            }

            override fun onErrorOccurred(
                playerId: String,
                error: Exception,
            ) {
                Log.d(TAG, "onErrorOccurred: $error")
            }

            override fun onVideoStatusChanged(
                playerId: String,
                state: BambuserVideoState,
                playerActions: PlayerActions,
            ) {
                Log.d(TAG, "onVideoStatusChanged: $state")
            }

            override fun onVideoProgress(
                playerId: String,
                duration: Long,
                currentTime: Long,
            ) {
                Log.d(TAG, "onVideoProgress: duration: $duration, currentTime: $currentTime")
            }
        }
    }

    sdkInstance.GetLiveView(
        modifier = modifier.fillMaxSize(),
        videoConfiguration = BambuserVideoConfiguration(
            events = listOf("*"),
            configuration = mapOf(
                "buttons" to mapOf(
                    "dismiss" to "event",
                ),
                "ui" to mapOf(
                    "hidePlaybackRateButton" to false,
                    "hidePromotedShows" to false,
                ),
                "autoplay" to true,
                "currency" to "USD",
                "locale" to "en-US",
            ),
            videoType = BambuserVideoAsset.Live(showId),
        ),
        videoPlayerDelegate = videoPlayerDelegate,
    )
}
