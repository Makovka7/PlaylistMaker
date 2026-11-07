package com.example.playlistmaker

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun formatDuration(ms: Long?): String {
    val seconds = (ms ?: 0L) / 1000
    return String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60)
}

fun plural(n: Int, one: String, few: String, many: String): String {
    val m100 = n % 100
    val m10 = n % 10
    return when {
        m100 in 11..14 -> many
        m10 == 1 -> one
        m10 in 2..4 -> few
        else -> many
    }
}

fun tracksCountText(n: Int) = "$n " + plural(n, "трек", "трека", "треков")

fun minutesText(n: Int) = "$n " + plural(n, "минута", "минуты", "минут")

fun Fragment.navigateTo(fragment: Fragment) {
    parentFragmentManager.beginTransaction()
        .replace(R.id.container, fragment)
        .addToBackStack(null)
        .commit()
}

fun Fragment.hideKeyboard() {
    val view: View = activity?.currentFocus ?: return
    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun ImageView.loadCover(model: Any?, radiusDp: Int) {
    Glide.with(this)
        .load(model)
        .placeholder(R.drawable.ic_music_note)
        .fallback(R.drawable.ic_music_note)
        .error(R.drawable.ic_music_note)
        .transform(CenterCrop(), RoundedCorners(context.dp(radiusDp)))
        .into(this)
}
