package com.esabook.indosat_adblock

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.esabook.indosat_adblock.databinding.SettingsActivityBinding
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

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

        lifecycleScope.launch {
            fetchConfig()
        }
    }

    private fun fetchConfig() {
        val frc = FirebaseRemoteConfig.getInstance()
        frc.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val host = frc.getString("host_frc")
                val source = frc.getString("source_frc")

                SettingPref.setBlockedHostFRC(host)
                SettingPref.setBlockedSourceFRC(source)

                Log.i("iii", host + "ooo" + source)
            } else {
                task.exception?.printStackTrace()
            }
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}