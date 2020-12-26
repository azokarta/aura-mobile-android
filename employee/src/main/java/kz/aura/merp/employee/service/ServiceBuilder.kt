package kz.aura.merp.employee.service

import android.content.Context
import kz.aura.merp.employee.util.Constants
import kz.aura.merp.employee.util.LanguageHelper.getLanguage
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kz.aura.merp.employee.util.Helpers.getToken
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit

object ServiceBuilder {

    private const val baseUrl = Constants.WERP_TEST

    fun<T> buildService(service: Class<T>, context: Context): T {
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
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(service);
    }
}