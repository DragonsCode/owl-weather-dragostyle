package com.example.oweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oweather.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE cityKey = :cityKey LIMIT 1")
    fun observeByCityKey(cityKey: String): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE cityKey = :cityKey LIMIT 1")
    suspend fun getByCityKey(cityKey: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: WeatherCacheEntity)
}
