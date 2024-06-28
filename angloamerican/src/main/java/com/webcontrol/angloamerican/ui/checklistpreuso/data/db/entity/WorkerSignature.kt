package com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signature")
class WorkerSignature(
    @NonNull
    @PrimaryKey
    @ColumnInfo(name="worker_id")
    var workerId: String,

    @NonNull
    @ColumnInfo(name="signature")
    var signature: String
)