# Bambuser Commerce SDK Demo App

## About
This Android application demonstrates how to integrate and utilize the Bambuser Commerce SDK to display live shopping shows within your app.

## Requirements
This SDK targets Android 35, and the minimal support API is 26 (Android 8+)
This SDK uses Compose BOM version 2025.02.00

## Setup

First, add a new maven repository to your dependency resolution management:

```kotlin
repositories {
        google()
        mavenCentral()
        // other repositories you might have...
        
        maven {
            url "https://repo.repsy.io/mvn/bambuser/bambuser-commerce-sdk"
        }
    }
```

Then add the dependency into your `app/build.gradle`:

```kotlin
implementation("com.bambuser:commerce-sdk:${insert_the_latest_version}")
```

## Initialize the SDK
You need to initialize the SDK before using it.
In your Application class, you can create multiple instances of the BambuserSDK:
```kotlin
globalBambuserSDK = BambuserSDK(
    applicationContext = this,
    organizationServer = OrganizationServer.US,
    )

euBambuserSDK = BambuserSDK(
    applicationContext = this,
    organizationServer = OrganizationServer.EU,
    )
```

You can choose your organization server from the OrganizationServer enum.
* OrganizationServer.US
* OrganizationServer.EU

## Create a new view to show a live show
You need to use the SDK instance to create a new Live view `sdkInstance.GetLiveView`
This function would require two mandatory parameters:
1. `videoConfiguration` - This is the configuration for the video player.
2. `videoPlayerDelegate` - This is the delegate to receive events and errors.

## Create a new view to show a Shoppable video
You need to use the SDK instance to create a new Shoppable view `sdkInstance.GetLShoppableVideoView`
This function would require the same two mandatory parameters:
1. `videoConfiguration` - This is the configuration for the video player.
2. `videoPlayerDelegate` - This is the delegate to receive events and errors.
**Note:** `videoConfiguration` for Shoppable videos is slightly different from Live videos.

And some optional parameters:
1`modifier` - This is the modifier to apply to the view.
2`playerId` - This a unique identifier for the player.

For `videoConfiguration` you need to Initialize a `BambuserVideoConfiguration` object.
`BambuserVideoConfiguration` takes three mandatory parameters:
1. `events` - This is the list of events you want to receive from the player.
2. `configuration` - This is the configuration for the video player, you can find more useful configurations in the [documentation](https://bambuser.com/docs/live/player-api-reference/#constants)
3. `videoType` - You can use `BambuserVideoAsset.Live(id)` passing the id as your show id, or `BambuserVideoAsset.Shoppable(id)` for shoppable video id.

For `videoPlayerDelegate` you need to Initialize a `BambuserVideoPlayerDelegate` object.
`BambuserVideoPlayerDelegate` has two methods:
1. `onNewEventReceived` - This is the method to receive events from the player.
2. `onErrorOccurred` - This is the method to receive errors from the player.
3. `onVideoStatusChanged` - This is the method to receive the status of the video. very useful if you want to play the video automatically.
4. `onVideoProgress` - This is the method to receive the progress of the video, very useful for analytics.

You can decide the logic for handling these events and errors.
You can put the video activity in PiP, and navigate to different part of your app.
Note: `onNewEventReceived` is tightly coupled with the interactive UI layer on top of our video player, so it's not recommended to use it in PiP mode.

In `onNewEventReceived` you can receive a reference for some actions you might want the player to do:
1. `invoke` You want to call one of the player functions, for example to hydrate your products.
This will require a function name and arguments passed as String.
2. `notifyView` used if the player is waiting for some inputs from you
All inputs will be coming with a callback key.
3. `switchScreenMode` This is only used for shoppable videos, 
you can switch between the enums 
    1.`ScreenMode.FullScreenMode` To enable full screen mode with full layout elements.
    2.`ScreenMode.PreviewMode` a default light version, you need to pass a specific configuration to this mode.

In `onVideoStatusChanged` You can receive the status of your player, and you will have reference to some player actions
1. `BambuserVideoState` is an enum class holds all available video states
2. `PlayerActions` is your interface to remotely control the player, either in the full mode or PiP mode, it should have (play, pause, mute and unMute) actions.

## Getting a list of shoppable videos
In order to get a list of shoppable videos, you can use `sdkInstance.getShoppableVideoPlayerCollection`
This is a suspended function that needs to operate under a coroutine scope.
This function will throw an exception if the request fails, or any errors happened during the request.
It retrieves a collection of shoppable videos based on the provided collection information
It supports fetching videos by playlist ID / page ID or by product SKU.

1.A Simple example for getting a list of shoppable videos by page:
This call will create a playlist called home if it doesn't exist

```kotlin
getShoppableVideoPlayerCollection(
    BambuserCollectionInfo.Playlist(
        pageId = "home",
        orgId = "$organizationId",
    ),
)
```

2.A Simple example for getting a list of shoppable videos by product SKU:

```kotlin
getShoppableVideoPlayerCollection(
    BambuserCollectionInfo.SKU(
        sku = "${product.sku}",
        orgId = "$organizationId",
    ),
)
```

## Conversion tracking
Bambuser Conversion Tracking for Live Video Shopping gives you the most value out of your Live Shopping performance statistics. 
The Bambuser Conversion tracker enables merchants to attribute the relevant conversions to the LiveShopping shows. 
The number of attributed sales will be available on the stats page of each show.

* In order to use the conversion tracking, you need to call the track function that is associated with your SDK instance.
* The track function is a suspended function that needs to operate under a coroutine scope.
* The track function takes two mandatory arguments
  1. `eventName` should be `purchase`
  2. `data` this is an map for all data needs to be sent, you can find a good example for event data from [here](https://bambuser.com/docs/live/conversion-tracking/)

A simple example:
```kotlin
lifecycleScope.launch {
    sdkInstance.track(
        eventName = "purchase",
        data = mapOf(
            "orderId" to "123456",
            "orderValue" to "12345",
            "orderProductIds" to listOf("1", "2", "3"),
            "currency" to "USD",
        ),
    )
}
```

## Picture-in-picture Experience:
To enable PiP for your video you need to 
1. Add `android:resizeableActivity="true"` in your AndroidManifest - application block.
2. Add ```android:supportsPictureInPicture="true" 
android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation"```
to your AndroidManifest - activity block.
3. You can either manage your PiP state and add it to `GetLiveView`
4. Or you can get use of the out of the box solution by extending your activity to `PiPDelegate` and delegate implementation to `PiPDelegateActivity`
for Example `class LiveActivity : ComponentActivity(), PiPDelegate by PiPDelegateActivity() `
5. In this case you would need to override:
   * `enterPiP()` and get the correct aspect ratio
   * `onPictureInPictureModeChanged` to update the PiP state
   * `onStop()` to be able to close the video after closing PiP
You can find a good example in `LiveActivity` implementation.
6. Don't rely on `onNewEventReceived` , nor any of the `ViewActions` functions `invoke` , `notifyView` and `switchScreenMode` in PiP mode.

### Preloading

By default, the SDK **preloads videos** to reduce startup time when playback begins. This ensures a smoother user experience by minimizing delays when calling `play` on a video.

If you prefer to **disable automatic preloading**, you can set the `preload` flag to `false` in your video configuration:

```kotlin
"preload" to false
```
