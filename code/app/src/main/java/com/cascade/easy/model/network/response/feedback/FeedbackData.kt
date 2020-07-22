package com.cascade.easy.model.network.response.feedback

import com.cascade.easy.model.network.base.Output
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeedbackData(
    val count: Int,
    val total: Int,
    val items: List<Feedback>
) : Output()