package com.esabook.indosat_adblock

import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.esabook.indosat_adblock.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: LogViewModel by viewModels()
    private val logAdapter by lazy { LogAdapter() }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // nothing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAdapter()

        binding.rvItem.adapter = logAdapter

        binding.btOnOff.setOnClickListener {
            gotoSetting()
        }

        lifecycleScope.launch {
            viewModel.logs.collectLatest { pagingData ->
                logAdapter.submitData(pagingData)
            }
        }

        SettingSync.syncRemoteConfig(this)

    }

    private fun gotoSetting(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val i = (getSystemService(ROLE_SERVICE) as? RoleManager)
                ?.createRequestRoleIntent(RoleManager.ROLE_BROWSER)

            if (i != null) {
                resultLauncher.launch(i)
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val key = ":settings:fragment_args_key"
            val value = "default_browser"
            val i = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            i.putExtra(key, value)
            i.putExtra(":settings:show_fragment_args", bundleOf(key to value))

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        } else {
            val name = getString(R.string.app_name)
            toast("Not possible. Goto Setting > Default App > Browser > Select $name")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val incrementTextSize = resources.getDimension(R.dimen._1sp)

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.action_up -> {
                logAdapter.textSize.value?.let { size ->
                    logAdapter.textSize.postValue(size + incrementTextSize)
                }
                true
            }

            R.id.action_down -> {
                logAdapter.textSize.value?.let { size ->
                    logAdapter.textSize.postValue(size - incrementTextSize)
                }
                true
            }

            R.id.settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btOnOff.imageTintList = if (isMeDefaultApp())
            getColorStateList(R.color.bt_on) else getColorStateList(R.color.bt_off)

    }

    private fun setupAdapter() {
        val defaultTextSize = SettingPref.getDefaultTextSize()

        logAdapter.textSize.value = defaultTextSize

        logAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && logAdapter.itemCount == 0
            binding.tvEmpty.isVisible = isListEmpty

            val isLoading = loadState.refresh is LoadState.Loading
            if (isLoading) binding.tvEmpty.isGone = true
        }
    }

    private fun isMeDefaultApp(): Boolean {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://indosatooredoo.com"))
        val infos = packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY)
        Log.i("iii isMeDefaultApp", infos?.activityInfo?.packageName.orEmpty())
        return packageName == infos?.activityInfo?.packageName
    }
}