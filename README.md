# Bambuser Commerce SDK Demo App

## About
This Android application demonstrates how to integrate and utilize the Bambuser Commerce SDK to display live shopping shows within your app.

## Requirements
This SDK targets Android 35, and the minimal support API is 26 (Android 8+)
This SDK uses Compose BOM version 2025.02.00

## Setup

First, add a new maven repository to your dependency resolution management:

```
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

```
implementation("com.bambuser:commerce-sdk:$insert the latest version")
```

## Initialize the SDK
You need to initialize the SDK before using it.
In your Application class, you can create multiple instances of the BambuserSDK:
```
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

And some optional parameters:
1`modifier` - This is the modifier to apply to the view.
2`playerId` - This a unique identifier for the player.

For `videoConfiguration` you need to Initialize a `BambuserVideoConfiguration` object.
`BambuserVideoConfiguration` takes three mandatory parameters:
1. `events` - This is the list of events you want to receive from the player.
2. `configuration` - This is the configuration for the video player, you can find more useful configurations in the [documentation](https://bambuser.com/docs/live/player-api-reference/#constants)
3. `videoType` - You can use `BambuserVideoAsset.Live(id)` passing the id as your show id.

For `videoPlayerDelegate` you need to Initialize a `BambuserVideoPlayerDelegate` object.
`BambuserVideoPlayerDelegate` has two methods:
1. `onNewEventReceived` - This is the method to receive events from the player.
2. `onErrorOccurred` - This is the method to receive errors from the player.

You can decide the logic for handling these events and errors.
You can put the video activity in PiP, and navigate to different part of your app. 

In `onNewEventReceived` you can receive a reference for some actions you might want the player to do:
1. `invoke` You want to call one of the player functions, for example to hydrate your products.
This will require a function name and arguments passed as String.
2. `notifyView` used if the player is waiting for some inputs from you
All inputs will be coming with a callback key.