package com.azatberdimyradov.gezmeyekmi.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "pictures_table")
@Parcelize
data class CityPhoto(
    val city: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val picture: ByteArray? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable
