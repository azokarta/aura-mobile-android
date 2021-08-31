package kz.aura.merp.employee.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kz.aura.merp.employee.base.AppPreferences

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): AppPreferences {
        val mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return AppPreferences(
            EncryptedSharedPreferences.create(
                "sharedPrefsFile",
                mainKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        )
    }
}