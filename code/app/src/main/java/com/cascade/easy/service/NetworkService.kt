package com.cascade.easy.service

import com.cascade.easy.manager.NetworkManager
import com.cascade.easy.model.network.request.Error
import com.cascade.easy.model.network.response.feedback.FeedbackData
import com.cascade.easy.model.network.response.log.Log
import com.cascade.easy.network.base.Listener
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NetworkService {
    @POST(NetworkManager.ERROR_PATH)
    fun logError(@Body error: Error): Listener<Log>

    @GET("feedback/list")
    fun feedbackList(): Listener<FeedbackData>
}