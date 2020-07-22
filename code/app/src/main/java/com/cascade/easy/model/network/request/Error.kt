package com.cascade.easy.model.network.request

import com.cascade.easy.model.network.base.Input
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Error(val message: String, val time: Date = Date()) : Input()