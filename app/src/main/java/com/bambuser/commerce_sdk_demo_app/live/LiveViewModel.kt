package com.bambuser.commerce_sdk_demo_app.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bambuser.commerce_sdk_demo_app.ui.UiState
import java.util.Locale
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LiveViewModel(
    private val api: LiveApi = LiveApi.instance,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<LiveVideo>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<LiveVideo>>> = _uiState

    private val _navigationEvent = Channel<String>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val channel = api.getChannel()
                val mapped = channel.playlists
                    .flatMap { it.shows }
                    .map { show ->
                        LiveVideo(
                            id = show.showId,
                            title = show.title,
                            preview = show.image,
                            duration = show.duration,
                            startedAt = show.startedAt,
                        )
                    }
                    .sortedWith(
                        compareByDescending<LiveVideo> { it.startedAt ?: 0L }
                            .thenBy { it.title }
                    )
                _uiState.value = UiState.Content(mapped)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load videos")
            }
        }
    }

    fun onVideoClicked(videoId: String) {
        viewModelScope.launch {
            _navigationEvent.send(videoId)
        }
    }

    fun formatDuration(seconds: Int?): String {
        if (seconds == null || seconds < 0) return "—:—"
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        } else {
            String.format(Locale.US, "%d:%02d", m, s)
        }
    }
}
