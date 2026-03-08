package com.example.oweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.oweather.data.local.dao.CityDao
import com.example.oweather.data.local.dao.WeatherCacheDao
import com.example.oweather.data.local.entity.CityEntity
import com.example.oweather.data.local.entity.WeatherCacheEntity

@Database(
    entities = [CityEntity::class, WeatherCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun weatherCacheDao(): WeatherCacheDao
}
