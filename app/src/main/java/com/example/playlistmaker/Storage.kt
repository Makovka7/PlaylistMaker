package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object Storage {
    private const val K_DARK = "dark_theme"
    private const val K_HISTORY = "history"
    private const val K_FAV = "favorites"
    private const val K_PLAYLISTS = "playlists"
    private const val HISTORY_MAX = 10

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("playlist_maker", Context.MODE_PRIVATE)
    }

    var darkTheme: Boolean
        get() = prefs.getBoolean(K_DARK, false)
        set(value) = prefs.edit().putBoolean(K_DARK, value).apply()

    private inline fun <reified T> read(key: String): List<T> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return try {
            gson.fromJson<List<T>>(json, object : TypeToken<List<T>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun write(key: String, value: Any) {
        prefs.edit().putString(key, gson.toJson(value)).apply()
    }

    // ---- история поиска ----
    fun history(): List<String> = read<String>(K_HISTORY)

    fun addToHistory(query: String) {
        val list = history().toMutableList()
        list.remove(query)
        list.add(0, query)
        write(K_HISTORY, list.take(HISTORY_MAX))
    }

    // ---- избранное ----
    fun favorites(): List<Track> = read<Track>(K_FAV)

    fun isFavorite(trackId: Long): Boolean = favorites().any { it.trackId == trackId }

    /** Возвращает новое состояние: true — трек теперь в избранном. */
    fun toggleFavorite(track: Track): Boolean {
        val list = favorites().toMutableList()
        val removed = list.removeAll { it.trackId == track.trackId }
        if (!removed) list.add(0, track)
        write(K_FAV, list)
        return !removed
    }

    // ---- плейлисты ----
    fun playlists(): List<Playlist> = read<Playlist>(K_PLAYLISTS)

    fun playlist(id: String): Playlist? = playlists().firstOrNull { it.id == id }

    fun addPlaylist(playlist: Playlist) = write(K_PLAYLISTS, playlists() + playlist)

    fun deletePlaylist(id: String) {
        playlist(id)?.coverPath?.let { File(it).delete() }
        write(K_PLAYLISTS, playlists().filter { it.id != id })
    }

    /** false — трек уже был в плейлисте (или плейлиста нет). */
    fun addTrackToPlaylist(playlistId: String, track: Track): Boolean {
        val all = playlists()
        val playlist = all.firstOrNull { it.id == playlistId } ?: return false
        if (playlist.tracks.any { it.trackId == track.trackId }) return false
        playlist.tracks.add(track)
        write(K_PLAYLISTS, all)
        return true
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: Long) {
        val all = playlists()
        all.firstOrNull { it.id == playlistId }?.tracks?.removeAll { it.trackId == trackId }
        write(K_PLAYLISTS, all)
    }
}
