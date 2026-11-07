package com.example.playlistmaker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _b: FragmentSearchBinding? = null
    private val b get() = _b!!

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { _b?.let { search(it.input.text.toString()) } }
    private var call: Call<SearchResponse>? = null

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentSearchBinding.bind(view)
        b.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        trackAdapter = TrackAdapter(onClick = { navigateTo(TrackFragment.newInstance(it)) })
        b.results.adapter = trackAdapter

        historyAdapter = HistoryAdapter { query ->
            b.input.setText(query)
            b.input.setSelection(query.length)
            search(query)
        }
        b.history.adapter = historyAdapter

        b.input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, b4: Int, c: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString().orEmpty()
                b.clear.isVisible = text.isNotEmpty()
                handler.removeCallbacks(searchRunnable)
                if (text.isBlank()) {
                    call?.cancel()
                    showHistoryOrNothing()
                } else {
                    handler.postDelayed(searchRunnable, DEBOUNCE_MS)
                }
            }
        })

        b.input.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && b.input.text.isNullOrEmpty()) showHistoryOrNothing()
        }

        b.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(b.input.text.toString())
                hideKeyboard()
                true
            } else false
        }

        b.clear.setOnClickListener {
            b.input.setText("")
            b.input.requestFocus()
        }

        showState()
    }

    private fun search(rawQuery: String) {
        val query = rawQuery.trim()
        handler.removeCallbacks(searchRunnable)
        if (query.isEmpty()) return
        call?.cancel()
        showState(progress = true)

        val request = Api.service.search(query)
        call = request
        request.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(c: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (_b == null || c.isCanceled) return
                if (!response.isSuccessful) {
                    showError()
                    return
                }
                val tracks = response.body()?.results.orEmpty().filter { !it.trackName.isNullOrBlank() }
                if (tracks.isEmpty()) {
                    showNothingFound()
                } else {
                    Storage.addToHistory(query)
                    trackAdapter.items = tracks
                    b.results.scrollToPosition(0)
                    showState(results = true)
                }
            }

            override fun onFailure(c: Call<SearchResponse>, t: Throwable) {
                if (_b == null || c.isCanceled) return
                showError()
            }
        })
    }

    private fun showHistoryOrNothing() {
        val history = Storage.history()
        historyAdapter.items = history
        showState(history = history.isNotEmpty() && b.input.hasFocus())
    }

    private fun showNothingFound() {
        b.placeholderImage.setImageResource(R.drawable.ic_placeholder_nothing)
        b.placeholderTitle.setText(R.string.nothing_found)
        b.placeholderMessage.isVisible = false
        showState(placeholder = true)
    }

    private fun showError() {
        b.placeholderImage.setImageResource(R.drawable.ic_placeholder_no_connection)
        b.placeholderTitle.setText(R.string.connection_problem)
        b.placeholderMessage.setText(R.string.connection_message)
        b.placeholderMessage.isVisible = true
        showState(placeholder = true)
    }

    private fun showState(
        history: Boolean = false,
        results: Boolean = false,
        progress: Boolean = false,
        placeholder: Boolean = false
    ) {
        b.history.isVisible = history
        b.results.isVisible = results
        b.progress.isVisible = progress
        b.placeholder.isVisible = placeholder
    }

    override fun onDestroyView() {
        handler.removeCallbacks(searchRunnable)
        call?.cancel()
        _b = null
        super.onDestroyView()
    }

    private companion object {
        const val DEBOUNCE_MS = 2000L
    }
}
