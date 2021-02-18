package kz.aura.merp.employee.data.service

import android.content.Context
import kz.aura.merp.employee.util.Constants
import kz.aura.merp.employee.util.LanguageHelper.getLanguage
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kz.aura.merp.employee.util.Helpers.getToken
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {

    fun <T> buildService(
        service: Class<T>,
        context: Context,
        baseUrl: String = Constants.WE_MOB_DEV
    ): T {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor { chain ->
                val token = getToken(context)
                var newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Cache-Control", "public, max-age=60")
                    .addHeader("Accept-Language", getLanguage(context))

                newRequest = if (baseUrl == Constants.WE_MOB_DEV) {
                    newRequest.addHeader("Authorization", token!!)
                } else {
                    newRequest.addHeader("Authorization", "Basic V0VNT0I6d2Vtb2I=")
                }

                chain.proceed(newRequest.build())
            }
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Converter required to convert JSON to objects
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(baseUrl) // Base part of address
            .build()

        return retrofit.create(service) // Create an object with which we will execute queries
    }
}