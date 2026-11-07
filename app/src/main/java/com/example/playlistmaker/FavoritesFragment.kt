package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _b: FragmentFavoritesBinding? = null
    private val b get() = _b!!
    private lateinit var adapter: TrackAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentFavoritesBinding.bind(view)
        b.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        adapter = TrackAdapter(onClick = { navigateTo(TrackFragment.newInstance(it)) })
        b.list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        val tracks = Storage.favorites()
        adapter.items = tracks
        b.list.isVisible = tracks.isNotEmpty()
        b.empty.isVisible = tracks.isEmpty()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
