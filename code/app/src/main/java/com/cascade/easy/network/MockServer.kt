package com.cascade.easy.network

import android.content.res.AssetManager
import com.cascade.easy.manager.NetworkManager
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object MockServer {
    private const val NOT_FOUND_MESSAGE = "Not found."

    private val executor by lazy { Executors.newSingleThreadExecutor() }
    private val mockWebServer by lazy { MockWebServer() }

    fun url(): HttpUrl = with(executor) {
        submit(Callable {
            mockWebServer.url("/")
        }).let {
            return it.get()
        }
    }

    fun start(assets: AssetManager) = executor.execute {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val language = request.getHeader(NetworkManager.LANGUAGE_HEADER_KEY)

                val fileName = StringBuilder().apply {
                    append("mock")
                    if (language != null) {
                        append("/$language")
                    }
                    append("${request.path}.json")
                }.toString()

                val content = try {
                    assets.open(fileName)
                        .bufferedReader()
                        .use(BufferedReader::readText)
                } catch (ex: Exception) {
                    null
                }

                val code = content?.let {
                    HttpURLConnection.HTTP_OK
                } ?: HttpURLConnection.HTTP_NOT_FOUND
                val body = content?.let {
                    content
                } ?: NOT_FOUND_MESSAGE

                return MockResponse()
                    .setResponseCode(code)
                    .setBody(body)
            }
        }
    }

    fun shutdown() = executor.execute {
        mockWebServer.shutdown()
    }
}