package com.example.oweather.core.model

data class City(
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val note: String? = null,
    val createdAt: Long
)
