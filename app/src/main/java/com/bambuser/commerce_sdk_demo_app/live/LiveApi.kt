package com.bambuser.commerce_sdk_demo_app.live

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://svc-prod-us.liveshopping.bambuser.com/"

interface LiveApi {
    @GET("widgets/channels/pNxCZkolKbw35xwZOltt")
    suspend fun getChannel(): ChannelResponse

    companion object {
        val instance: LiveApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LiveApi::class.java)
        }
    }
}

data class ChannelResponse(
    @SerializedName("playlists") val playlists: List<PlaylistDto>,
)

data class PlaylistDto(
    @SerializedName("shows") val shows: List<ShowDto>,
)

data class ShowDto(
    @SerializedName("showId") val showId: String,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("startedAt") val startedAt: Long?,
)
