package com.example.oweather.di

import android.content.Context
import androidx.room.Room
import com.example.oweather.data.local.AppDatabase
import com.example.oweather.data.local.dao.CityDao
import com.example.oweather.data.local.dao.WeatherCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather_app.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCityDao(database: AppDatabase): CityDao = database.cityDao()

    @Provides
    fun provideWeatherCacheDao(database: AppDatabase): WeatherCacheDao = database.weatherCacheDao()
}
