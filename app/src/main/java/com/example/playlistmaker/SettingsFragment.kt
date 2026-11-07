package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentAgreementBinding
import com.example.playlistmaker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentSettingsBinding.bind(view)
        b.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        b.themeSwitch.isChecked = Storage.darkTheme
        b.themeSwitch.setOnCheckedChangeListener { _, checked ->
            Storage.darkTheme = checked
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        b.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            }
            startSafely(Intent.createChooser(intent, getString(R.string.share_app)))
        }

        b.support.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            }
            startSafely(intent)
        }

        b.agreement.setOnClickListener { navigateTo(AgreementFragment()) }
    }

    private fun startSafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.no_app_found, Toast.LENGTH_SHORT).show()
        }
    }
}

class AgreementFragment : Fragment(R.layout.fragment_agreement) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FragmentAgreementBinding.bind(view).toolbar
            .setNavigationOnClickListener { parentFragmentManager.popBackStack() }
    }
}
