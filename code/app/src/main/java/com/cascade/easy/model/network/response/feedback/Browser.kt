package com.cascade.easy.model.network.response.feedback

import com.cascade.easy.model.network.base.Output
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Browser(
    val online: Boolean,
    val cookieEnabled: Boolean,
    val product: String,
    val appCodeName: String,
    val userAgent: String,
    val platform: String,
    val appVersion: String,
    val appName: String,
    val vendorSub: String,
    val language: String
) : Output()