package com.esabook.indosat_adblock

import android.app.Activity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

object SettingSync {

    fun syncRemoteConfig(activity: Activity) {
        val frc = FirebaseRemoteConfig.getInstance()
        frc.fetchAndActivate().addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val host = frc.getString("host_frc")
                val source = frc.getString("source_frc")

                SettingPref.setBlockedHostFRC(host)
                SettingPref.setBlockedSourceFRC(source)
            } else {
                task.exception?.printStackTrace()
            }
        }
    }
}