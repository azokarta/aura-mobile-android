package kz.aura.merp.employee.util

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kz.aura.merp.employee.Application
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Interceptor @Inject constructor(
    @ApplicationContext val context: Context
) : Interceptor {
    @Volatile private var host: HttpUrl? = null
    private var link: Link = Link.MAIN

    fun setHost(link: Link) {
        this.link = link
        this.host = HttpUrl.parse(defineUri(link))
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var newUrl: HttpUrl = chain.request().url()

        if (host != null) {
            newUrl = chain.request().url().newBuilder()
                .scheme(host!!.scheme())
                .host(host!!.url().toURI().host)
                .port(host!!.port())
                .build()
        }

        var newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Cache-Control", "public, max-age=60")
            .addHeader("Content-Language", LanguageHelper.getLanguage(context))
        newRequest = if (link != Link.AUTH) {
            newRequest.addHeader("Authorization", "Bearer ${getToken(context)}")
        } else {
            newRequest.addHeader("Authorization", "Basic V0VNT0I6d2Vtb2I=")
        }

        return chain.proceed(newRequest.build())
    }
}