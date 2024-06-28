package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "answer_question", primaryKeys = ["id"])
class AnswerQuestion(
    @NonNull
    @ColumnInfo(name="id")
    var id: Int,

    @NonNull
    @ColumnInfo(name="id_check")
    var idCheck: Int,

    @NonNull
    @ColumnInfo(name="id_check_det")
    var idCheckDet: Int,

    @NonNull
    @ColumnInfo(name="id_checking")
    var idChecking: Int,

    @NonNull
    @ColumnInfo(name="id_checking_Head")
    var idCheckingHead: Int,

    @ColumnInfo(name="id_tipo")
    var idTipo: String,

    @ColumnInfo(name="nombre_check")
    var nombreCheck: String,

    @ColumnInfo(name="usr_crea")
    var usrCrea: String,
    )