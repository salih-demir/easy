package com.cascade.easy.model.preference

import android.os.Parcelable
import com.cascade.easy.app.Configuration
import com.cascade.easy.data.NightMode
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings(
    val language: String = Configuration.DEFAULT_LANGUAGE,
    val nightMode: NightMode = Configuration.DEFAULT_DARK_MODE
) : Parcelable