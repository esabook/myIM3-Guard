package com.esabook.indosat_adblock

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceFragmentCompat
import com.esabook.indosat_adblock.databinding.SettingsActivityBinding

class SettingActivity : AppCompatActivity() {

    private val binding by lazy {
        SettingsActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        SettingSync.syncRemoteConfig(this)

    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val defaultAppPref = findPreference<DropDownPreference>(getString(R.string.pkey_default_app))

            val customEntries: Array<CharSequence> = listOf("Not Set").plus( getSupportedBrowserApp()).toTypedArray()
            val customValues: Array<CharSequence> = customEntries

            defaultAppPref?.entries = customEntries
            defaultAppPref?.entryValues = customValues
        }


        private fun getSupportedBrowserApp(): List<CharSequence> {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://indosatooredoo.com"))
            val infos = requireContext().packageManager
                .queryIntentActivities(i, PackageManager.MATCH_ALL)
                .filterNot { it.activityInfo.packageName == activity?.packageName }

            return infos.map { it.activityInfo.packageName }
        }
    }
}