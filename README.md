# Bambuser Commerce SDK Demo App

## About
This Android application demonstrates how to integrate and utilize the Bambuser Commerce SDK to display live shopping shows within your app.

**Current SDK version:** 4.0.0 (released 2026-10-05).

## Requirements
This SDK targets Android 36, and the minimal support API is 26 (Android 8+)
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
3. `videoType` - You can use `BambuserVideoAsset.Live(id)` passing the id as your show id, or `BambuserVideoAsset.Shoppable(id)` for shoppable video id. Since 4.0.0 a `BambuserCollectionInfo` is also accepted here, but only for `GetAppearanceView` (see below).

And one optional parameter:
4. `videoScaleMode` - `BambuserVideoScaleMode.FIT` letterboxes the video, `BambuserVideoScaleMode.FILL` covers the whole player view and crops the overflow. Since 4.0.0 it defaults to `null`, which lets the player's resize events pick the mode (falling back to `FIT`). Set it explicitly if you want a fixed framing.

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

## Appearance view (Hub-configured layouts)

Available since 4.0.0.

`sdkInstance.GetAppearanceView` renders a collection of shoppable videos using the appearance configured in Bam Hub. The SDK fetches the collection, picks the layout from the appearance's `mode` (grid, row, story or fab), applies its sizing, spacing, autoplay and tap behavior, and loads further pages as the user scrolls. If `mode` is missing or unknown the row layout is used and the state reports `BambuserAppearanceMode.UNKNOWN`.

```kotlin
val appearanceState = rememberBambuserAppearanceViewState()

sdkInstance.GetAppearanceView(
    videoConfiguration = BambuserVideoConfiguration(
        videoType = BambuserCollectionInfo.Playlist(
            orgId = "<orgId>",
            componentId = "<placement id from Bam Hub>",
        ),
        configuration = mapOf(
            "playerConfig" to mapOf(
                "softLimit" to "6",   // appearance override, same keys as the hub
                "currency" to "USD",  // anything else goes to the web player as-is
            ),
            "thumbnail" to mapOf("showPlayButton" to false), // optional, see below
            "preload" to true,                               // optional, the default
        ),
        events = listOf("*"),                        // player events forwarded to the delegate
        videoScaleMode = BambuserVideoScaleMode.FILL, // optional, every player in the collection
    ),
    pageSize = 15,
    state = appearanceState,
    appearanceDelegate = object : BambuserAppearanceViewDelegate {
        override fun onPlayerSelected(item: BambuserAppearanceItem) { /* ... */ }
    },
    videoPlayerDelegate = myPlayerDelegate,    // per-player events for every player inside
    modifier = Modifier.fillMaxWidth(),
)
```

### Parameters
1. `videoConfiguration` (mandatory) - What to render and how every player in it is set up. Details below.
2. `videoPlayerDelegate` (mandatory) - Receives events, errors and status changes for every player in the collection, the same `BambuserVideoPlayerDelegate` you use for a single shoppable video.
3. `pageSize` (optional, default 15) - Number of videos fetched per page. Further pages load automatically when the layout is scrolled past 80% of its content, until the last page or the appearance's soft limit is reached.
4. `appearanceDelegate` (optional) - Collection-level callbacks, see [Delegate](#delegate).
5. `state` (optional) - A `BambuserAppearanceViewState` created with `rememberBambuserAppearanceViewState()`, see [State](#state).
6. `modifier` (optional) - Applied to the view, see [Sizing](#sizing).

### Source
The configuration's `videoType` must be a `BambuserCollectionInfo`, otherwise the composable throws `IllegalArgumentException`.
* `BambuserCollectionInfo.Playlist(orgId, componentId)` renders a placement with the appearance configured for it in Bam Hub. The page URL the hub's targeting rules see is derived from the component id.
* `BambuserCollectionInfo.SKU(orgId, sku)` and `BambuserCollectionInfo.GroupId(orgId, groupId)` render the videos attached to a product or a product group, with whatever appearance the response carries.

Changing the configuration starts the collection over from page 1.

### Overrides
A map under `configuration["playerConfig"]` takes the keys the hub sends, for example `mode`, `focusMode`, `autoplay`, `cornerRadius`, `playerWidth`, `playlistGap` and `softLimit`.
* Precedence, lowest first: the collection's `playerConfig`, the default appearance, the app appearance, then this map.
* Null and blank values in it are ignored, so an unset key falls back to the resolved value instead of clearing it.
* Entries that are not appearance keys, such as `currency`, `locale` or `buttons`, are forwarded unchanged to every player's web configuration, the same as `playerConfig` on a single shoppable player. `currency` is mandatory if you hydrate products.
* Any other top-level key of `configuration` is copied into every player's configuration as well.

### Players
`events` and `videoScaleMode` on the configuration apply to every player the collection creates, inline and fullscreen, exactly as they do for a single shoppable player. Product hydration works the same way too: answer `provide-product-data` in `onNewEventReceived` with `viewAction.invoke("updateProductWithData", ...)` for each product id the player asks about.

### Thumbnail
A map under `configuration["thumbnail"]` tunes the preview thumbnail of every player, with the same keys as a single shoppable player: `enabled`, `showPlayButton`, `showLoadingIndicator`, `preview` and `contentMode` (`scaleToFill`, `scaleAspectFit` or `scaleAspectFill`). `enabled = false` hides the thumbnail where the SDK would show one, and `showPlayButton` wins over the hub's setting. The FAB and the fullscreen overlay never show a thumbnail, whatever the map says.

### Preload
`configuration["preload"]` defaults to `true`: every player boots the web player behind its thumbnail so playback starts at once. Set it to `false` to defer that until the player is tapped or played, which saves memory and network on long collections.

### State
`rememberBambuserAppearanceViewState()` returns a `BambuserAppearanceViewState` the SDK keeps up to date as Compose state:
* `mode` - the resolved `BambuserAppearanceMode` (`GRID`, `ROW`, `STORY`, `FAB` or `UNKNOWN`).
* `videoIds` - the list of video IDs currently loaded.
* `pagination` - a `BambuserAppearancePagination` (`page`, `pageSize`, `total`, `totalPages`), or `null` before the first page loads.

Use it to size or decorate the container per layout. The handle lives as long as the composable that created it.

### Delegate
All methods of `BambuserAppearanceViewDelegate` have empty defaults and run on the main thread. Item callbacks carry a `BambuserAppearanceItem` with the video's `index` in `videoIds`, its `playerId` and its `metadata` (`VideoMetadata`: id, title, preview, length, hasAudio).

```kotlin
object : BambuserAppearanceViewDelegate {
    // Paging. Page 1 loads after the composable enters the composition, so these start at 1.
    override fun onWillLoadPage(page: Int) {}
    override fun onPageLoaded(page: Int, totalPages: Int?) {}
    override fun onPageFailedToLoad(page: Int, error: Exception) {}

    // A player was tapped. The SDK then does what the appearance's focusMode says.
    override fun onPlayerSelected(item: BambuserAppearanceItem) {}

    // Fullscreen overlay, when focusMode opens one. Exit reports the video the user ended on.
    override fun onEnterFullscreen(item: BambuserAppearanceItem) {}
    override fun onExitFullscreen(item: BambuserAppearanceItem) {}

    // The active video changed: cascade moved on, FAB advanced, or the user paged in fullscreen.
    override fun onCurrentChanged(item: BambuserAppearanceItem) {}

    // The collection closed itself, for example from the FAB close button.
    override fun onDismiss() {}
}
```

### Sizing
Grid fills the space given by `modifier`. Row and story take the full width and size their height from the appearance's player size. The FAB floats over the content inside the bounds of `modifier`, sizes itself and has its own close button.

### Lifecycle
The collection lives as long as the composable is in the composition. Leaving the composition cancels any fetch in flight and releases the players; there is nothing to clean up. `onDismiss` only fires when the collection closes itself, not when it leaves the composition.

> **Important:** the FAB behaves and looks different from the other layouts. Create a separate appearance in Bam Hub for it, on its own placement, instead of reusing the one for grid, row or story.

## Getting a list of shoppable videos
In order to get a list of shoppable videos, you can use `sdkInstance.getShoppableVideoPlayerCollectionMetadata`
This is a suspended function that needs to operate under a coroutine scope.
This function will throw an exception if the request fails, or any errors happened during the request.
It retrieves a collection of shoppable videos based on the provided collection information
It supports fetching videos by playlist ID / page ID or by product SKU.

It returns a `BambuserCollectionMetadata` object, which holds:
* `videoMetadataList` - a list of `VideoMetadata`, one per video, with `videoId`, `preview`, `title`, `length`, and `hasAudio`.
* `pagination` - pagination information for the collection.

**Note:** `getShoppableVideoPlayerCollection` is deprecated in favor of `getShoppableVideoPlayerCollectionMetadata`.

1.A Simple example for getting a list of shoppable videos by page:
This call will create a playlist called home if it doesn't exist

```kotlin
getShoppableVideoPlayerCollectionMetadata(
    BambuserCollectionInfo.Playlist(
        pageId = "home",
        orgId = "$organizationId",
    ),
)
```

2.A Simple example for getting a list of shoppable videos by product SKU:

```kotlin
getShoppableVideoPlayerCollectionMetadata(
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
