package kz.aura.merp.customer.service

import android.content.Context
import kz.aura.merp.customer.util.Constants
import kz.aura.merp.customer.util.Helpers.getToken
import kz.aura.merp.customer.util.LanguageHelper.getLanguage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    fun<T> buildService(service: Class<T>, context: Context, uri: String = Constants.WERP_MOB_TEST): T {

        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor { chain ->
                    val token = getToken(context)
                    val bearer = "Bearer $token"

                    var newRequest = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("Cache-Control", "public, max-age=60")
                            .addHeader("Accept-Language", getLanguage(context))
                    if (token!!.isNotEmpty()) {
                        newRequest = newRequest.addHeader("Authorization", bearer)
                    }
                    chain.proceed(newRequest.build())
                }
                .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(uri)
            .build()

        return retrofit.create(service);
    }
}
//val retrofit = Retrofit.Builder()
//        .addConverterFactory(GsonConverterFactory.create())
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
//        .baseUrl(uri)
//        .build()