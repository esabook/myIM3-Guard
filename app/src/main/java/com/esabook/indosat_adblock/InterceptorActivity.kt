package com.esabook.indosat_adblock

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InterceptorActivity : AppCompatActivity() {

    private val toUriFlags = Intent.URI_ALLOW_UNSAFE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {

            var blockedHost: List<String>
            var blockedSource: List<String>
            var uriStr: String
            var isBlocked = false

            withContext(Dispatchers.Default) {
                blockedHost = SettingPref.getBlockedHost()
                    ?.trim()
                    ?.split(";")
                    ?.filterNot(String::isEmpty)
                    .orEmpty()

                blockedSource = SettingPref.getDBlockedSource()
                    ?.trim()
                    ?.split(";")
                    ?.filterNot(String::isEmpty)
                    .orEmpty()

                uriStr = intent
                    .toUri(toUriFlags)

                val httpUri = uriStr.toUri()
                val host = httpUri.host
                for (h in blockedHost) {
                    if (isBlocked) break
                    isBlocked = host?.contains(h.toRegex()) == true
                }

                val source = httpUri.getQueryParameter("source")
                for (s in blockedSource) {
                    if (isBlocked) break
                    isBlocked = source?.contains(s.toRegex()) == true
                }

            }



            if (isBlocked) {
                log("blocked", bundle = bundleOf("uri" to uriStr))
                saveLog(uriStr)
                toast("Memblokir")
                finish()
            } else {
                try {
                    log("not-blocked", bundle = bundleOf("uri" to uriStr))
                    redirectToExternalApp()

                } catch (e: Exception) {
                    e.printStackTrace()
                    finish()
                }
            }
        }
    }

    private suspend fun saveLog(uriStr: String) {
        withContext(Dispatchers.IO) {
            App.db.logDao().insert(
                LogEntity(
                    uri = uriStr,
                    inString = intent.toString(),
                    timestamp = System.currentTimeMillis()
                )
            )
        }

    }

    private fun redirectToExternalApp() {
        val ti = createTargetIntent()
        val pm = packageManager
            .queryIntentActivities(ti, PackageManager.MATCH_ALL)
            .filterNot{ it.activityInfo.packageName == packageName}

        if (pm.isEmpty()){
            startActivity(Intent.createChooser(ti, null))

        } else {
            val targetDefaultApp = SettingPref.getDefaultBrowserApp()
            val targetActivityInfo = pm.firstOrNull { it.activityInfo.packageName == targetDefaultApp }

            if (targetActivityInfo != null) {
                Intent(ti).also {
                    it.setPackage(targetActivityInfo.activityInfo.packageName)
                    startActivity(it)
                }
            } else {
                LaunchChooserBottomsheet()
                    .setData(ti, pm)
                    .show(supportFragmentManager, null)
            }

        }
    }

    var allowResumed = true
    override fun onResume() {
        super.onResume()

        if (!allowResumed) {
            finish()
        }

        allowResumed = false
    }


    private fun createTargetIntent(): Intent {
        val newIntent = Intent().apply {
            action = intent.action
            intent.extras?.let { putExtras(it) }
            setDataAndType(intent.data, intent.type)
            flags = intent.flags
        }
        return newIntent
    }



}