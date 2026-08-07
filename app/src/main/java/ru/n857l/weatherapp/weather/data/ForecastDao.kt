package ru.n857l.weatherapp.weather.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.n857l.weatherapp.weather.domain.ForecastData
import ru.n857l.weatherapp.weather.domain.ForecastDay
import ru.n857l.weatherapp.weather.domain.ForecastHour

@Dao
interface ForecastDao {

    @Query("SELECT * FROM forecast_cache WHERE id = 0")
    suspend fun getForecast(): ForecastCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveForecast(forecast: ForecastCacheEntity)
}

@Entity(tableName = "forecast_cache")
data class ForecastCacheEntity(
    @PrimaryKey
    val id: Int = 0,
    val lat: Float,
    val lon: Float,
    val dateTime: Long,
    val hoursJson: String,
    val daysJson: String
) {

    fun toDomain(): ForecastData {
        val gson = Gson()
        val hours: List<ForecastHour> = gson.fromJson(
            hoursJson,
            object : TypeToken<List<ForecastHour>>() {}.type
        )
        val days: List<ForecastDay> = gson.fromJson(
            daysJson,
            object : TypeToken<List<ForecastDay>>() {}.type
        )
        return ForecastData(hours = hours, days = days)
    }

    companion object {
        fun from(
            lat: Float,
            lon: Float,
            dateTime: Long,
            data: ForecastData
        ): ForecastCacheEntity {
            val gson = Gson()
            return ForecastCacheEntity(
                lat = lat,
                lon = lon,
                dateTime = dateTime,
                hoursJson = gson.toJson(data.hours),
                daysJson = gson.toJson(data.days)
            )
        }
    }
}