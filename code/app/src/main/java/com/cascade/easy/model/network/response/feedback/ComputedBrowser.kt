package com.cascade.easy.model.network.response.feedback

import com.cascade.easy.model.network.base.Output
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ComputedBrowser(
    @SerializedName("Browser")
    val browser: String,
    @SerializedName("Version")
    val version: String,
    @SerializedName("Platform")
    val platform: String,
    @SerializedName("FullBrowser")
    val fullBrowser: String
) : Output()