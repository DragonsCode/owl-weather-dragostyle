package com.example.oweather.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.oweather.presentation.cities.CitiesScreen
import com.example.oweather.presentation.citydetails.CityDetailsScreen
import com.example.oweather.presentation.main.MainScreen
import com.example.oweather.presentation.map.MapScreen
import com.example.oweather.presentation.splash.SplashScreen

@Composable
fun WeatherApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route
    ) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.Main.route) {
            MainScreen(
                onOpenCities = { navController.navigate(AppRoute.Cities.route) },
                onOpenMap = { navController.navigate(AppRoute.Map.route) },
                onOpenCityDetails = { cityId ->
                    navController.navigate(AppRoute.CityDetails.createRoute(cityId))
                }
            )
        }

        composable(AppRoute.Cities.route) {
            CitiesScreen(
                onBack = { navController.popBackStack() },
                onCityOpen = { cityId ->
                    navController.navigate(AppRoute.CityDetails.createRoute(cityId))
                }
            )
        }

        composable(
            route = AppRoute.CityDetails.route,
            arguments = listOf(
                navArgument(AppRoute.CityDetails.CITY_ID_ARG) {
                    type = NavType.LongType
                }
            )
        ) {
            CityDetailsScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.Map.route) {
            MapScreen(onBack = { navController.popBackStack() })
        }
    }
}
