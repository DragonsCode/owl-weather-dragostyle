package com.example.oweather.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.oweather.data.local.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM cities ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities WHERE id = :cityId LIMIT 1")
    suspend fun getById(cityId: Long): CityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity): Long

    @Update
    suspend fun update(city: CityEntity)

    @Delete
    suspend fun delete(city: CityEntity)
}
