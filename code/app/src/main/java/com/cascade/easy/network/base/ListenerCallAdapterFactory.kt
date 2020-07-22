package com.cascade.easy.network.base

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

private class ListenerCallAdapter<T>(
    private val responseType: Type,
    private val executor: Executor?
) : CallAdapter<T, Listener<T>> {
    override fun responseType() = responseType

    override fun adapt(call: Call<T>): Listener<T> {
        return Listener(call, executor)
    }
}

class ListenerCallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() =
            ListenerCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Listener::class.java != getRawType(returnType)) {
            return null
        } else if (returnType !is ParameterizedType) {
            val typeName = Listener::class.simpleName
            throw IllegalStateException("$typeName return type must be parameterized as $typeName<Foo>")
        }

        val responseType = getParameterUpperBound(0, returnType)
        return ListenerCallAdapter<Any>(
            responseType,
            retrofit.callbackExecutor()
        )
    }
}