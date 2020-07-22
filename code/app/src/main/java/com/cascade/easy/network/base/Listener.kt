package com.cascade.easy.network.base

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors

typealias Success<T> = ((T) -> Unit)?
typealias Error = ((Throwable) -> Unit)?

object EmptyBodyException : IllegalStateException("Response body cannot be empty!")

class Listener<T>(private val call: Call<T>, private val callbackExecutor: Executor?) {
    private fun executeCallback(block: () -> Unit) {
        Runnable {
            block()
        }.let {
            if (callbackExecutor == null) {
                it.run()
            } else {
                callbackExecutor.execute(it)
            }
        }
    }

    private fun <T> handleSuccess(body: T, success: Success<T>) = executeCallback {
        success?.invoke(body)
    }

    private fun handleError(throwable: Throwable, error: Error) = executeCallback {
        error?.invoke(throwable)
    }

    private fun handleResponse(response: Response<T>, success: Success<T>, error: Error) {
        if (success != null && response.isSuccessful) {
            val body = response.body()
            if (body == null) {
                handleError(EmptyBodyException, error)
            } else {
                handleSuccess(body, success)
            }
        } else if (error != null) {
            handleError(HttpException(response), error)
        }
    }

    private fun sendSync(success: Success<T>, error: Error) {
        try {
            SYNC_REQUEST_EXECUTOR.execute {
                val response = call.execute()
                handleResponse(response, success, error)
            }
        } catch (ex: Exception) {
            handleError(ex, error)
        }
    }

    private fun sendAsync(success: Success<T>, error: Error) {
        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                handleError(t, error)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                handleResponse(response, success, error)
            }
        })
    }

    fun send(success: Success<T> = null, error: Error = null, async: Boolean = true) {
        if (async) {
            sendAsync(success, error)
        } else {
            sendSync(success, error)
        }
    }

    companion object {
        private val SYNC_REQUEST_EXECUTOR by lazy {
            Executors.newCachedThreadPool()
        }
    }
}