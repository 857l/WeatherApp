package ru.n857l.weatherapp.findcity.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Dao
interface FindCityDao {

    @Query("SELECT * FROM find_city WHERE id = 0")
    fun getCity(): FindCityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCity(city: FindCityEntity)
}

@Entity(tableName = "find_city")
data class FindCityEntity(
    @PrimaryKey
    val id: Int = 0,
    val lat: Float,
    val lon: Float
)