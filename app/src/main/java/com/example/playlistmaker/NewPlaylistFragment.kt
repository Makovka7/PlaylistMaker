package com.example.playlistmaker

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import java.io.File
import java.util.UUID

class NewPlaylistFragment : Fragment(R.layout.fragment_new_playlist) {

    private var _b: FragmentNewPlaylistBinding? = null
    private val b get() = _b!!
    private var coverPath: String? = null

    private val picker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { saveCover(it) }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentNewPlaylistBinding.bind(view)
        b.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        coverPath?.let { showCover(it) }
        b.cover.setOnClickListener { picker.launch("image/*") }

        b.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, bf: Int, c: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                b.create.isEnabled = !s.isNullOrBlank()
            }
        })
        b.create.isEnabled = !b.name.text.isNullOrBlank()

        b.create.setOnClickListener {
            val name = b.name.text.toString().trim()
            if (name.isEmpty()) return@setOnClickListener
            Storage.addPlaylist(
                Playlist(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = b.description.text.toString().trim(),
                    coverPath = coverPath,
                    tracks = mutableListOf(),
                    createdAt = System.currentTimeMillis()
                )
            )
            Toast.makeText(requireContext(), "Плейлист «$name» создан", Toast.LENGTH_SHORT).show()
            hideKeyboard()
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveCover(uri: Uri) {
        try {
            val dir = File(requireContext().filesDir, "covers").apply { mkdirs() }
            val file = File(dir, UUID.randomUUID().toString() + ".jpg")
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            coverPath = file.absolutePath
            showCover(file.absolutePath)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCover(path: String) {
        b.cover.setPadding(0, 0, 0, 0)
        b.cover.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this).load(File(path)).centerCrop().into(b.cover)
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
