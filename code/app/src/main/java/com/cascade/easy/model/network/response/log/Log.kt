package com.cascade.easy.model.network.response.log

import com.cascade.easy.model.network.base.Output
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Log(val message: String) : Output()