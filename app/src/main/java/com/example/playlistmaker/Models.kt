package com.example.playlistmaker

data class Track(
    val trackId: Long = 0,
    val trackName: String? = null,
    val artistName: String? = null,
    val trackTimeMillis: Long? = null,
    val artworkUrl100: String? = null
) {
    fun bigCoverUrl(): String? = artworkUrl100?.replace("100x100bb", "512x512bb")
}

data class Playlist(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val coverPath: String? = null,
    val tracks: MutableList<Track> = mutableListOf(),
    val createdAt: Long = 0
)

data class SearchResponse(val results: List<Track> = emptyList())
