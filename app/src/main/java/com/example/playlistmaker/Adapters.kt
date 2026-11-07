package com.example.playlistmaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemHistoryBinding
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.databinding.ItemTrackBinding
import java.io.File

class TrackAdapter(
    private val onClick: (Track) -> Unit,
    private val onLongClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackAdapter.VH>() {

    var items: List<Track> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class VH(val b: ItemTrackBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val track = items[position]
        holder.b.name.text = track.trackName.orEmpty()
        holder.b.info.text = "${track.artistName.orEmpty()} • ${formatDuration(track.trackTimeMillis)}"
        holder.b.cover.loadCover(track.artworkUrl100, 2)
        holder.b.root.setOnClickListener { onClick(track) }
        holder.b.root.setOnLongClickListener {
            onLongClick?.invoke(track)
            onLongClick != null
        }
    }
}

class PlaylistAdapter(private val onClick: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistAdapter.VH>() {

    var items: List<Playlist> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class VH(val b: ItemPlaylistBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val playlist = items[position]
        holder.b.name.text = playlist.name
        holder.b.count.text = tracksCountText(playlist.tracks.size)
        holder.b.cover.loadCover(playlist.coverPath?.let { File(it) }, 2)
        holder.b.root.setOnClickListener { onClick(playlist) }
    }
}

class HistoryAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.VH>() {

    var items: List<String> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class VH(val b: ItemHistoryBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val query = items[position]
        holder.b.query.text = query
        holder.b.root.setOnClickListener { onClick(query) }
    }
}
