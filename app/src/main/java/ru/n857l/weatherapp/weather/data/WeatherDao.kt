package ru.n857l.weatherapp.weather.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import ru.n857l.weatherapp.weather.domain.WeatherInCity

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE id = 0")
    suspend fun getWeather(): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather")
    suspend fun clear()
}

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey
    val id: Int = 0,
    val cityName: String,
    val lat: Float,
    val lon: Float,
    val temperature: Float,
    val feelsTemperature: Float,
    val tempMin: Float,
    val tempMax: Float,
    val pressure: Int,
    val humidity: Int,
    val seaLevelPressure: Int,
    val groundLevelPressure: Int,
    val speed: Float,
    val degree: Int,
    val gust: Float,
    val clouds: Int,
    val visibility: Int,
    val dateTime: Long,
    val sunrise: Long,
    val sunset: Long
) {

    fun toDomain(): WeatherInCity = WeatherInCity(
        lat = lat,
        lon = lon,
        cityName = cityName,
        temperature = temperature,
        feelsTemperature = feelsTemperature,
        tempMin = tempMin,
        tempMax = tempMax,
        pressure = pressure,
        humidity = humidity,
        seaLevelPressure = seaLevelPressure,
        groundLevelPressure = groundLevelPressure,
        speed = speed,
        degree = degree,
        gust = gust,
        clouds = clouds,
        visibility = visibility,
        dateTime = dateTime,
        sunrise = sunrise,
        sunset = sunset
    )

    fun toEntity(): WeatherEntity = WeatherEntity(
        cityName = cityName,
        lat = lat,
        lon = lon,
        temperature = temperature,
        feelsTemperature = feelsTemperature,
        tempMin = tempMin,
        tempMax = tempMax,
        pressure = pressure,
        humidity = humidity,
        seaLevelPressure = seaLevelPressure,
        groundLevelPressure = groundLevelPressure,
        speed = speed,
        degree = degree,
        gust = gust,
        clouds = clouds,
        visibility = visibility,
        dateTime = dateTime,
        sunrise = sunrise,
        sunset = sunset
    )
}