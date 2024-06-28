package com.webcontrol.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

@Entity(tableName = "geofencing")
data class Geofencing(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @SerializedName("GeofenceId")
    @ColumnInfo(name = "geofenceId")
    val geofenceId: String,

    @SerializedName("Date")
    val date: String,

    @SerializedName("Time")
    val time: String,

    @SerializedName("Latitude")
    val latitude: Double,

    @SerializedName("Longitude")
    val longitude: Double,

    @SerializedName("EventType")
    val event: String,

    val status: String,

    @SerializedName("WorkerId")
    val rut: String
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
