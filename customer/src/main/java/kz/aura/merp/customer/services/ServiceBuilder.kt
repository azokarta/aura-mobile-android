package kz.aura.merp.customer.services

import kz.aura.merp.customer.utils.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    fun<T> buildService(service: Class<T>, uri: String = Constants.WERP_MOB_TEST): T {

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(uri)
            .build()

        return retrofit.create(service);
    }
}