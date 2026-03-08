package com.example.oweather.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Main : AppRoute("main")
    data object Cities : AppRoute("cities")
    data object Map : AppRoute("map")

    data object CityDetails : AppRoute("city_details/{cityId}") {
        const val CITY_ID_ARG = "cityId"
        fun createRoute(cityId: Long): String = "city_details/$cityId"
    }
}
