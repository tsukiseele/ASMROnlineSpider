package com.tsukiseele.utils

import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.SocketException
import java.util.concurrent.TimeUnit

class OkHttpUtil private constructor(private val client: OkHttpClient) {
    companion object {
        private val TIMEOUT = 16
        private var client: OkHttpClient? = null

        fun init(client: OkHttpClient) {
            this.client = client
        }

        fun getClient(): OkHttpClient {
            if (client == null) {
                synchronized(OkHttpUtil::class.java) {
                    if (client == null) {
                            client = OkHttpClient.Builder()
                                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .retryOnConnectionFailure(true)
                                .build()
                    }
                }
            }
            return client!!
        }

        @Throws(IOException::class)
        @JvmOverloads
        operator fun get(url: String, headers: Map<String, String>? = null): Response {
            val requestBuilder = Request.Builder()
            requestBuilder.url(url)
            if (headers != null)
                requestBuilder.headers(Headers.of(headers))
            val res = getClient().newCall(requestBuilder.build()).execute()
            if (res.isSuccessful) {
                return res
            } else {
                res.close()
                throw SocketException("Failed: ${res.code()} ${res.message()}")
            }
        }
        @Throws(IOException::class)
        @JvmOverloads
        operator fun get(url: String, headers: Headers?): Response {
            val requestBuilder = Request.Builder()
            requestBuilder.url(url)
            if (headers != null)
                requestBuilder.headers(headers)
            val res = getClient().newCall(requestBuilder.build()).execute()
            if (res.isSuccessful) {
                return res
            } else {
                res.close()
                throw SocketException("Failed: ${res.code()} ${res.message()}")
            }
        }
    }
}
