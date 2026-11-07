package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private var _b: FragmentPlaylistsBinding? = null
    private val b get() = _b!!
    private lateinit var adapter: PlaylistAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentPlaylistsBinding.bind(view)
        b.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        adapter = PlaylistAdapter { navigateTo(PlaylistFragment.newInstance(it.id)) }
        b.list.adapter = adapter
        b.fab.setOnClickListener { navigateTo(NewPlaylistFragment()) }
    }

    override fun onResume() {
        super.onResume()
        adapter.items = Storage.playlists()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
