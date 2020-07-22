package com.cascade.easy.data

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.cascade.easy.R

enum class NightMode(
    @AppCompatDelegate.NightMode val systemValue: Int,
    @StringRes val labelRes: Int
) {
    ENABLED(AppCompatDelegate.MODE_NIGHT_YES, R.string.label_enabled),
    DISABLED(AppCompatDelegate.MODE_NIGHT_NO, R.string.label_disabled),
    FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.label_system)
}