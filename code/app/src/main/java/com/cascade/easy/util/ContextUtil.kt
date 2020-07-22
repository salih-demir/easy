package com.cascade.easy.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.view.View
import androidx.core.view.doOnPreDraw
import java.util.*

tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun Context.createConfigurationForLocale(locale: Locale): Configuration {
    return resources.configuration.apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
}

fun Context.toast(
    message: CharSequence,
    onShown: ((view: View) -> Unit)? = null
) {
    ToastUtil.buildToast(activity()!!, message).apply {
        view.doOnPreDraw {
            onShown?.invoke(it)
        }
    }.show()
}