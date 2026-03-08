# Weather Forecast App (OWeather)

Android-приложение прогноза погоды на Kotlin + Jetpack Compose.

## 1. Выполнение требований

### Базовые требования
- Kotlin + AndroidX + Material3: выполнено.
- UI: Jetpack Compose: выполнено.
- MVVM + Repository + Coroutines + Flow: выполнено.
- DI: Hilt: выполнено.
- Network: Retrofit + OkHttp + Gson: выполнено.
- Local storage: Room + DataStore: выполнено.
- Location: FusedLocationProviderClient: выполнено.
- Maps: Google Maps (Maps Compose) + fallback без ключа: выполнено.
- Weather API: Open-Meteo (без ключа): выполнено.

### Экраны
- Splash -> Main: реализовано.
- Main: текущая погода, 7-дневный прогноз, кнопки обновления/города/карта: реализовано.
- Cities: CRUD, поиск через geocoding, сохранение в Room: реализовано.
- City details: погода выбранного города: реализовано.
- Map: текущая позиция + маркеры городов; при отсутствии ключа инструкция: реализовано.
- Анимации: fade/scale на Splash, анимированное появление прогноза: реализовано.

### CRUD (Room)
- Таблица `cities`: `id, name, latitude, longitude, note, createdAt`.
- Таблица `weather_cache`: кэш прогноза по `cityKey` (включая `current_location`).
- Create/Read/Update/Delete для городов: реализовано.

### Offline-first
- При запуске и просмотре городов читается локальный кэш из Room.
- После отображения кэша выполняется обновление из сети.
- При сетевой ошибке используется fallback из `assets/weather_fallback.json`.

### Permissions
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `INTERNET` в `AndroidManifest.xml`.
- Runtime permission для локации реализован.
- При отказе приложение работает через ручной выбор города.

## 2. Архитектура

Слои:
- `presentation`: Compose экраны + ViewModel.
- `domain`: интерфейсы репозиториев + контракт локации.
- `data`: Room DAO/entities, Retrofit API, репозитории, DataStore, fallback assets.
- `di`: Hilt-модули.

Паттерны:
- MVVM
- Repository
- Dependency Injection
- Offline-first cache strategy

## 3. Использованные темы (8+)
- Jetpack Compose
- Navigation Compose
- Material3
- Hilt DI
- Retrofit + OkHttp
- Room
- DataStore Preferences
- Coroutines + Flow
- Fused Location Provider
- Google Maps Compose
- JSON parsing (Gson)
- Fallback data from assets

## 4. Как запустить

1. Откройте проект в Android Studio.
2. Убедитесь, что установлен Android SDK для `compileSdk 36`.
3. Sync Gradle.
4. Соберите проект:
   ```bash
   ./gradlew :app:assembleDebug
   ```
5. Запустите на эмуляторе/устройстве (Android 13+, `minSdk 33`).

## 5. Куда добавить Google Maps API Key

Файл:
- `app/src/main/res/values/strings.xml`

Поле:
- `google_maps_key`

Замените значение `ADD_YOUR_MAPS_API_KEY_HERE` на реальный ключ.

Если ключ не добавлен, экран карты показывает понятную инструкцию и ссылку на docs.

## 6. Какие скриншоты сделать для защиты

1. Splash экран.
2. Main: текущая погода и 7-дневный прогноз.
3. Main без разрешения локации (fallback на выбранный город).
4. Cities: поиск + результаты geocoding.
5. Cities: список сохраненных городов с CRUD.
6. City details: погода конкретного города.
7. Map с маркерами (или экран инструкции без ключа).
8. Демонстрация offline/fallback (отключить интернет и показать данные).

## 7. Важные файлы

- `app/src/main/java/com/example/oweather/navigation/WeatherApp.kt`
- `app/src/main/java/com/example/oweather/presentation/main/MainScreen.kt`
- `app/src/main/java/com/example/oweather/presentation/cities/CitiesScreen.kt`
- `app/src/main/java/com/example/oweather/presentation/citydetails/CityDetailsScreen.kt`
- `app/src/main/java/com/example/oweather/presentation/map/MapScreen.kt`
- `app/src/main/java/com/example/oweather/data/repository/WeatherRepositoryImpl.kt`
- `app/src/main/java/com/example/oweather/data/local/AppDatabase.kt`
- `app/src/main/assets/weather_fallback.json`
