package com.esabook.indosat_adblock

import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow


object SettingPref {

    private val pref: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.context)
    }

    private val ctx by lazy {
        App.context
    }

    fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            ctx.resources.displayMetrics
        )
    }

    fun Context.getKey(int: Int) = getString(int)

    fun getDefaultTextSize(): Float {
        val set = pref.getInt(ctx.getKey(R.string.pkey_textsize), 6).toFloat()
        return spToPx(set)
    }

    fun getBlockedHost(): String? {
        val set1 = pref.getString(ctx.getKey(R.string.pkey_host), "")
        val set2 = pref.getString(ctx.getKey(R.string.pkey_host_rc), "")
        return set2.plus(";").plus(set1)
    }

    fun getDBlockedSource(): String? {
        val set1 = pref.getString(ctx.getKey(R.string.pkey_source), "")
        val set2 = pref.getString(ctx.getKey(R.string.pkey_source_rc), "")
        return set2.plus(";").plus(set1)
    }

    fun getDefaultBrowserApp(): String? {
        return pref.getString(ctx.getKey(R.string.pkey_default_app), "")
    }

    fun setBlockedHostFRC(h: String) {
        pref.edit { putString(ctx.getKey(R.string.pkey_host_rc), h) }
    }

    fun setBlockedSourceFRC(s: String) {
        pref.edit { putString(ctx.getKey(R.string.pkey_source_rc), s) }
    }

}