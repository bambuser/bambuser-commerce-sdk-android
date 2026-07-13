# Changelog

## Every entry should be marked with one of following tags

- [Added] -- for new additions
- [Changed] -- for changes in existing functionality/files
- [Deprecated] -- for soon-to-be removed features
- [Removed] -- for now removed features/files
- [Fixed] -- for any bug fixes


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