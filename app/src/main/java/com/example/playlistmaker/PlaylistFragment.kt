package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import java.io.File
import java.util.Calendar

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private var _b: FragmentPlaylistBinding? = null
    private val b get() = _b!!
    private lateinit var playlistId: String
    private lateinit var adapter: TrackAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentPlaylistBinding.bind(view)
        playlistId = requireArguments().getString(ARG_ID).orEmpty()
        b.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        adapter = TrackAdapter(
            onClick = { navigateTo(TrackFragment.newInstance(it)) },
            onLongClick = { confirmRemoveTrack(it) }
        )
        b.list.adapter = adapter
        b.menu.setOnClickListener { showMenu() }
    }

    override fun onResume() {
        super.onResume()
        bind()
    }

    private fun bind() {
        val playlist = Storage.playlist(playlistId)
        if (playlist == null) {
            parentFragmentManager.popBackStack()
            return
        }
        b.cover.loadCover(playlist.coverPath?.let { File(it) }, 8)
        b.name.text = playlist.name
        b.description.text = playlist.description
        b.description.isVisible = playlist.description.isNotEmpty()

        val year = Calendar.getInstance().apply { timeInMillis = playlist.createdAt }.get(Calendar.YEAR)
        b.year.text = year.toString()

        val minutes = (playlist.tracks.sumOf { it.trackTimeMillis ?: 0L } / 60000).toInt()
        b.stats.text = "${minutesText(minutes)} • ${tracksCountText(playlist.tracks.size)}"
        adapter.items = playlist.tracks.toList()
    }

    private fun showMenu() {
        PopupMenu(requireContext(), b.menu).apply {
            menu.add(R.string.delete_playlist)
            setOnMenuItemClickListener {
                confirmDeletePlaylist()
                true
            }
            show()
        }
    }

    private fun confirmDeletePlaylist() {
        val name = Storage.playlist(playlistId)?.name.orEmpty()
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_playlist_question, name))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                Storage.deletePlaylist(playlistId)
                parentFragmentManager.popBackStack()
            }
            .show()
    }

    private fun confirmRemoveTrack(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.remove_track_question)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                Storage.removeTrackFromPlaylist(playlistId, track.trackId)
                bind()
            }
            .show()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "id"

        fun newInstance(id: String) = PlaylistFragment().apply {
            arguments = bundleOf(ARG_ID to id)
        }
    }
}
