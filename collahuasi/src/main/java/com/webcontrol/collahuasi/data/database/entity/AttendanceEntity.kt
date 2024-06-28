package com.webcontrol.collahuasi.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val rut: String,

    val date: String,

    val time: String,

    @ColumnInfo(name = "in_out")
    val inOut: String,

    val local: String,

    val company: String,

    val ost: String,

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("long")
    val longitude: Double
) {
}