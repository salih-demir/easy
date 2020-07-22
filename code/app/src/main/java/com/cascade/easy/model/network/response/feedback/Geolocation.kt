package com.cascade.easy.model.network.response.feedback

import com.cascade.easy.model.network.base.Output
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Geolocation(
    val lat: Double,
    val lon: Double,
    val country: String,
    val region: String,
    val city: String
) : Output()