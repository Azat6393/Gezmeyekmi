package com.azatberdimyradov.gezmeyekmi.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "cities_table")
@Parcelize
data class City(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val note: String,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable{
    val createDateFormatted: String
        get() = DateFormat.getDateInstance().format(created)
}