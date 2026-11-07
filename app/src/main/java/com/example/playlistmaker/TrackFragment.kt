package com.example.playlistmaker

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.databinding.SheetAddToPlaylistBinding

class TrackFragment : Fragment(R.layout.fragment_track) {

    private var _b: FragmentTrackBinding? = null
    private val b get() = _b!!
    private lateinit var track: Track

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentTrackBinding.bind(view)
        track = Gson().fromJson(requireArguments().getString(ARG_TRACK), Track::class.java)

        b.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        b.cover.loadCover(track.bigCoverUrl(), 8)
        b.name.text = track.trackName.orEmpty()
        b.artist.text = track.artistName.orEmpty()
        b.duration.text = formatDuration(track.trackTimeMillis)

        updateFavoriteIcon(Storage.isFavorite(track.trackId))
        b.favorite.setOnClickListener { updateFavoriteIcon(Storage.toggleFavorite(track)) }
        b.addToPlaylist.setOnClickListener { showPlaylistsSheet() }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            b.favorite.setImageResource(R.drawable.ic_favorite_filled)
            b.favorite.imageTintList =
                ColorStateList.valueOf(requireContext().getColor(R.color.heart_red))
        } else {
            b.favorite.setImageResource(R.drawable.ic_favorite_outline)
            b.favorite.imageTintList =
                ColorStateList.valueOf(requireContext().getColor(R.color.white))
        }
    }

    private fun showPlaylistsSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sheet = SheetAddToPlaylistBinding.inflate(layoutInflater)
        val playlists = Storage.playlists()
        sheet.empty.isVisible = playlists.isEmpty()
        sheet.list.adapter = PlaylistAdapter { playlist ->
            val added = Storage.addTrackToPlaylist(playlist.id, track)
            val message = if (added) {
                "Добавлено в плейлист «${playlist.name}»"
            } else {
                "Трек уже добавлен в плейлист «${playlist.name}»"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }.also { it.items = playlists }
        dialog.setContentView(sheet.root)
        dialog.show()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_TRACK = "track"

        fun newInstance(track: Track) = TrackFragment().apply {
            arguments = bundleOf(ARG_TRACK to Gson().toJson(track))
        }
    }
}
