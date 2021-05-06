package kz.aura.merp.employee.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private var token: String = ""

    fun setHost(link: Link) {
        this.link = link
        this.host = HttpUrl.parse(defineUri(link))
    }

    fun setToken(token: String) {
        this.token = token
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
            .addHeader("Accept-Language", LanguageHelper.getLanguage(context))
        newRequest = if (link != Link.AUTH) {
            newRequest.addHeader("Authorization", "Bearer $token")
        } else {
            newRequest.addHeader("Authorization", "Basic V0VNT0I6d2Vtb2I=")
        }

        return chain.proceed(newRequest.build())
    }
}