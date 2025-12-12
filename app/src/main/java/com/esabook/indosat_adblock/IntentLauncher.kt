package com.esabook.indosat_adblock

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.util.Log

object IntentLauncher {
    fun launchIntentExcludingSelf(act: Activity, i: Intent, chooserTitle: String?) {
        i.setComponent(null)
        Log.d("iii setcomp= null", i.toURI())
        val pm = act.packageManager
        val resolvedInfoList =
            pm.queryIntentActivities( i, 0) // Or PackageManager.MATCH_DEFAULT_ONLY

        for (resolveInfo in resolvedInfoList) {
            Log.d("iii resolveInfo", resolveInfo.activityInfo.packageName)
            if (resolveInfo.activityInfo.packageName != act.packageName) {
                if (resolvedInfoList.size == 1 && resolvedInfoList[0].activityInfo.packageName != act.packageName) {
                    i.setComponent(
                        ComponentName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name
                        )
                    )
                    act.startActivity(i)
                    act.finish()
                    return
                }
            }
        }

        // If no non-self default or multiple options exist, show a chooser excluding our app
        val targetedIntents: MutableList<Intent?> = ArrayList()
        for (resolveInfo in resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName != act.packageName) {
                val targetIntent = Intent(i)
                targetIntent.setComponent(
                    ComponentName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name
                    )
                )
                targetedIntents.add(targetIntent)
            }
        }

        if (!targetedIntents.isEmpty()) {
            val chooserIntent = Intent.createChooser(targetedIntents.removeAt(0), chooserTitle)
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                targetedIntents.toTypedArray<Intent?>()
            )
            act.startActivity(chooserIntent)
            act.finish()
        }

    }
}