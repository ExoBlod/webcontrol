package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worker_signature")
class WorkerSignatures(
    @NonNull
    @PrimaryKey
    @ColumnInfo(name="workerId")
    var workerId: String,

    @NonNull
    @ColumnInfo(name="workerPhoto")
    var workerPhoto: String
)