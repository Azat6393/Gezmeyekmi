package com.azatberdimyradov.gezmeyekmi.data


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.azatberdimyradov.gezmeyekmi.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [City::class, CityPhoto::class], version = 1)
abstract class CityDatabase: RoomDatabase() {

    abstract fun cityDao(): CityDao

    class Callback @Inject constructor(
        private val database: Provider<CityDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().cityDao()
            /*applicationScope.launch {
                dao.insert(City(name = "Karabuk",latitude = 39.214096,longitude = 35.310965))
            }*/
        }
    }
}