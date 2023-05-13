package com.edu.happytesting.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitApi {

    private var retrofit: Retrofit? = null

    /**
     * OkHttpClient used to specify the timeout for the api request and to add the
     * interceptor.
     */
    private fun okHttpClientSever(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .build()

    /**
     * retrofitInstance builds the networks call by adding the baseurl and preferred clients,
     * gson conversion factory and call adapter
     */
   private var mHttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

   private var mOkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(mHttpLoggingInterceptor)
        .build()

    private var gson: Gson? = GsonBuilder()
        .setLenient()
        .create()

    fun retrofitInstance(): Retrofit =
        retrofit
            ?: Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson!!))
                .baseUrl(Constant.API_URL)
                .client(okHttpClientSever())
                .client(mOkHttpClient)
                .build()
}
object Constant{
     const val API_URL="https://happytestings.com/SRM/php/"
}