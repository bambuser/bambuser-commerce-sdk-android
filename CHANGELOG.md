# Changelog

## Every entry should be marked with one of following tags

- [Added] -- for new additions
- [Changed] -- for changes in existing functionality/files
- [Deprecated] -- for soon-to-be removed features
- [Removed] -- for now removed features/files
- [Fixed] -- for any bug fixes

## [3.5.0] - 2026-08-19
### Added
- `BambuserCollectionInfo.GroupId(orgId, groupId)` for fetching a shoppable video collection by product item group ID, alongside the existing playlist/page ID and SKU options.
- Optional `contentMode` key in the shoppable `thumbnail` configuration, controlling how the poster image is scaled inside the player view. Accepted values are `"scaleToFill"`, `"scaleAspectFit"`, and `"scaleAspectFill"`; unrecognized values fall back to `"scaleAspectFit"`. Omitting the key keeps the previous fill-and-crop behavior.
- Two optional live viewer count callbacks on `BambuserVideoPlayerDelegate`:
  - `onCurrentViewerCountChanged(playerId: String, viewers: Long)` — approximate number of viewers watching right now.
  - `onTotalViewerCountChanged(playerId: String, viewers: Long)` — accumulated views over the lifetime of the broadcast. Not deduplicated per viewer, so replays are counted again.

  Both have default implementations, so existing integrations need no changes. Only live broadcasts report meaningful values. Counts are pushed by the backend rather than polled: they arrive at its cadence, fire once per update received, and stop arriving while the connection is down. Neither callback is affected by the `events` filter in `BambuserVideoConfiguration`.
- The viewer count can now be shown in the player UI for live shows using the native player, if it is enabled in BamHub. No integration work is required.

### Changed
- All SDK network requests now use explicit timeouts (10s connect, 15s read) instead of waiting indefinitely. This applies to collection requests, player startup, tracking, and thumbnail image downloads.
- Network failures now report the actual error type and message (for example `SocketTimeoutException: timeout`) instead of a bare `"Exception: null"`, and player startup failures name the failing request and HTTP status code.

### Fixed
- Shoppable thumbnails now fill the player's host view instead of being confined to the video rect reported by the player. Small host views (e.g. story-circle or grid-cell layouts) could previously show the poster as a thin letterboxed strip. Tapping anywhere on the poster now starts playback, matching the larger thumbnail area.
- Closing video players and then opening new ones could leave the new players stuck on a black screen, with no way to recover other than restarting the app. Opening and closing players any number of times now works reliably.
- The player no longer waits forever on a stalled internet connection; it now gives up after a short time and reports a clear error instead of showing nothing.
- Every player now either reports "ready" or reports an error within 60 seconds. Previously, some failures produced complete silence.
- The very first events from a player could be lost before the app started listening. All events are now buffered and delivered.
- Closing a player now fully releases everything it was using (video, network activity, and background work), so leftovers from a closed player can no longer interfere with new players or waste battery and memory.
- Reopening the same video after closing it now starts playback again instead of showing a dead player.
- Playback status events for the `waiting` and `ready` states no longer report a hardcoded `muted = false`, which could reset the mute control in your UI. Mute state is now only reported by the events that actually carry it.
- `getShoppableVideoPlayerCollection` and `getShoppableVideoPlayerCollectionMetadata` now always perform their network I/O on a background dispatcher, so calling them from the main thread no longer risks a `NetworkOnMainThreadException`.

## [3.4.1] - 2026-07-28
### Removed
- Leftover launcher icon resources.

## [3.4.0] - 2026-07-24
### Fixed
- Fix video thumbnail glitch
- Enhance event tracking


## [3.3.0] - 2026-07-13
### Added
- `getShoppableVideoPlayerCollectionMetadata` to `BambuserSDK` for retrieving collection metadata (`BambuserCollectionMetadata` with per-video `VideoMetadata`), supporting fetching by playlist/page ID or by SKU.

### Deprecated
- `getShoppableVideoPlayerCollection` in favor of `getShoppableVideoPlayerCollectionMetadata`.

## [3.2.0] - 2026-06-26
### Added
- `seek(to: Long)` function to `PlayerActions` and `BamPlayerViewModel` to seek the player to a given position.

## [3.1.0] 2026-6-15
### Fixed
- Fix malformed event error when a player event's `event` payload is not a JSON object, safely handling array (`"list"`), primitive (`"value"`), and null payloads, as well as non-string `type` and `callbackKey` fields.

## [3.0.0] -2026-5-26
### Added
- `componentId` support to `BambuserCollectionInfo.Playlist`.

### Changed
- Bump compileSdk and targetSdk to 36.

### Removed
- `playerId` parameter from `GetLiveView` and `GetLShoppableVideoView`.

### Deprecated
- `pageId`, `containerId`, and `title` fields in `BambuserCollectionInfo.Playlist`.

### Fixed
- Some internal improvements and bug fixes

## [2.6.0] - 2026-2-26
### Added
- Internal fixes and improvements

## [2.5.0] - 2026-2-09

### Added
- Support to playback speed rate control

## [2.2.0] - 2025-12-10

### Added
- Adding support to video pre-loading configuration.

## [2.2.0] - 2025-11-26

### Added
- Adding mute and un mute functions to PlayerActions.

## [2.1.1] - 2025-10-06

### Fixed
- Web view initialization with autoplay

## [2.1.0] - 2025-09-26

### Added
- onVideoStatusChanged to BambuserVideoPlayerDelegate.
- onVideoProgressChanged to BambuserVideoPlayerDelegate.
- PlayerActions to be able to stop pause videos after the player initialization.
### Fixed
- Improved tracking.

## [2.0.1] - 2025-09-10

### Removed
- Compose Koin dependency

## [2.0.0] - 2025-09-02

### Added
- Shoppable videos support
- Show case for video collections

### Fixed
- Fixed video pause in the background bug