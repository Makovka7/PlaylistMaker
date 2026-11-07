package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentMainBinding.bind(view)
        b.menuSearch.setOnClickListener { navigateTo(SearchFragment()) }
        b.menuPlaylists.setOnClickListener { navigateTo(PlaylistsFragment()) }
        b.menuFavorites.setOnClickListener { navigateTo(FavoritesFragment()) }
        b.menuSettings.setOnClickListener { navigateTo(SettingsFragment()) }
    }
}
