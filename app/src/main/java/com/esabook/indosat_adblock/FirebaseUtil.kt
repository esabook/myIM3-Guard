package com.esabook.indosat_adblock

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

fun Context.log(name: String, bundle: Bundle?) =
    FirebaseAnalytics.getInstance(this).logEvent(name, bundle)