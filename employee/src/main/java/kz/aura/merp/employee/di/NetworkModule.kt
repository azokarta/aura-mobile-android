//package kz.aura.merp.employee.di
//
//import dagger.Provides
//import kz.aura.merp.employee.data.network.AuthApi
//import kz.aura.merp.employee.util.Link
//import kz.aura.merp.employee.util.defineUri
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//import javax.inject.Singleton
//
//object NetworkModule {
//
//    @Singleton
//    @Provides
//    fun provideHttpClient() : OkHttpClient {
//        return OkHttpClient.Builder()
//            .readTimeout(15, TimeUnit.SECONDS)
//            .connectTimeout(15, TimeUnit.SECONDS)
//            .build()
//    }
//
//    @Singleton
//    @Provides
//    fun provideConverterFactory(): GsonConverterFactory {
//        return GsonConverterFactory.create()
//    }
//
//    @Singleton
//    @Provides
//    fun provideRetrofitInstance(
//        okHttpClient: OkHttpClient,
//        gsonConverterFactory: GsonConverterFactory,
//        link: Link
//    ): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(defineUri(link))
//            .client(okHttpClient)
//            .addConverterFactory(gsonConverterFactory)
//            .build()
//    }
//
//    @Singleton
//    @Provides
//    fun provideAuthApi(retrofit: Retrofit): AuthApi {
//        return retrofit.create(AuthApi::class.java)
//    }
//
//}