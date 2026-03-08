package com.example.oweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    val results: List<GeoResultDto>?
)

data class GeoResultDto(
    val id: Long?,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    @SerializedName("admin1")
    val admin1: String?
)
