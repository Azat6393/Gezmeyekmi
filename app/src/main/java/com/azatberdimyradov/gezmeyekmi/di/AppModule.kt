package com.azatberdimyradov.gezmeyekmi.di

import android.app.Application
import androidx.room.Room
import com.azatberdimyradov.gezmeyekmi.data.CityDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule{

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: CityDatabase.Callback
    ) = Room.databaseBuilder(app, CityDatabase::class.java, "city_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: CityDatabase) = db.cityDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
