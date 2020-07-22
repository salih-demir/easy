package com.cascade.easy.model.network.response.feedback

import com.cascade.easy.model.network.base.Identity
import com.cascade.easy.model.network.base.Output
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Feedback(
    override val id: String,
    val rating: Int?,
    val comment: String?,
    val browser: Browser?,
    @SerializedName("computed_browser")
    val computedBrowser: ComputedBrowser?,
    @SerializedName("geo")
    val geolocation: Geolocation?,
    val labels: List<String>?
) : Identity, Output()