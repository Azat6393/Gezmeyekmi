package com.azatberdimyradov.gezmeyekmi.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    fun getCitiesWithFilter(query: String, sortOrder: SortOrder): Flow<List<City>> =
        when(sortOrder){
            SortOrder.BY_DATE -> getCitiesSortedByDateCreated(query)
            SortOrder.BY_NAME -> getCitiesSortedByName(query)
        }

    @Query("SELECT * FROM cities_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY created")
    fun getCitiesSortedByDateCreated(searchQuery: String): Flow<List<City>>

    @Query("SELECT * FROM cities_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name")
    fun getCitiesSortedByName(searchQuery: String): Flow<List<City>>

    @Query("SELECT * FROM cities_table")
    fun getCities(): Flow<List<City>>

    @Query("SELECT * FROM pictures_table WHERE city LIKE :name")
    fun getCityPicture(name: String): Flow<List<CityPhoto>>

    @Query("SELECT * FROM pictures_table")
    fun getAllCityPictures(): Flow<List<CityPhoto>>

    @Query("SELECT * FROM pictures_table WHERE city = :name")
    suspend fun getDeletedPictures(name: String): List<CityPhoto>

    @Query("DELETE FROM pictures_table WHERE city LIKE :name")
    suspend fun deletePictures(name: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: City)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(cityPhoto: CityPhoto)

    @Update
    suspend fun update(city: City)

    @Delete
    suspend fun delete(city: City)

    @Delete
    suspend fun deletePicture(cityPhoto: CityPhoto)

}