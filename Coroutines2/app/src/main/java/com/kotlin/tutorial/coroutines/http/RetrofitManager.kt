package com.kotlin.tutorial.coroutines.http

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kotlin.tutorial.coroutines.api.APIService
import com.safframework.http.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

import retrofit2.converter.gson.GsonConverterFactory

import kotlin.jvm.java

/**
 *
 * @FileName:
 *          com.kotlin.tutorial.retrofit.app1.http.RetrofitManager.java
 * @author: Tony Shen
 * @date: 2018-06-18 19:19
 * @version V1.0 <描述当前版本功能>
 */
class RetrofitManager private constructor() {

    private val apiService: APIService

    private val okhttpClient: OkHttpClient

    init {

        val builder = OkHttpClient.Builder()

        //设置超时
        builder.writeTimeout((5 * 1000).toLong(), TimeUnit.MILLISECONDS)
        builder.readTimeout((5 * 1000).toLong(), TimeUnit.MILLISECONDS)
        builder.connectTimeout((5 * 1000).toLong(), TimeUnit.MILLISECONDS)

        val loggingInterceptor = LoggingInterceptor.Builder()
                .loggable(true) // TODO: 发布到生产环境需要改成false
                .request()
                .requestTag("Request")
                .response()
                .responseTag("Response")
                .printThreadName(false)
                .build()

        //设置拦截器
        builder.addInterceptor(loggingInterceptor)

        okhttpClient = builder.build()

        mRetrofit = Retrofit.Builder()
                .baseUrl(APIService.API_BASE_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create()) //设置 gson 转换器
                .addCallAdapterFactory(CoroutineCallAdapterFactory()) // 设置协程的适配器
                .client(okhttpClient)
                .build()

        apiService = mRetrofit.create(APIService::class.java)
    }

    fun retrofit(): Retrofit = mRetrofit

    fun apiService(): APIService = apiService

    fun okhttpClient(): OkHttpClient = okhttpClient

    private object Holder {
        val MANAGER = RetrofitManager()
    }

    companion object {

        private lateinit var mRetrofit: Retrofit

        @JvmStatic
        fun get(): RetrofitManager {

            return Holder.MANAGER
        }
    }
}