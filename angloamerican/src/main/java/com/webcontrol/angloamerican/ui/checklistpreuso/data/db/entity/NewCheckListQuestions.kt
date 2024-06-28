package com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListQuestion
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeAnswer


@Entity(tableName = "new_check_list_question",indices = [Index(value = ["id_check_group", "orden"], unique = true)])
class NewCheckListQuestions (
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id: Int =0,

    @NonNull
    @ColumnInfo(name="descripcion")
    var descripcion: String,

    @NonNull
    @ColumnInfo(name="id_check")
    var idCheck: Int,

    @NonNull
    @ColumnInfo(name="id_checkDet")
    var idCheckDet: Int,

    @NonNull
    @ColumnInfo(name="id_check_group")
    var idCheckGroup: Int,

    @NonNull
    @ColumnInfo(name="orden")
    var orden: Int,

    @NonNull
    @ColumnInfo(name="id_tipo")
    var idTipo: String,

    @NonNull
    @ColumnInfo(name="nombre_check_group")
    var nombreCheckGroup: String,

    @NonNull
    @ColumnInfo(name="reg_foto")
    var regFoto: String,

    @NonNull
    @ColumnInfo(name="tipo")
    var tipo: String,

    @NonNull
    @ColumnInfo(name="nombre")
    var nombre: String,

    @NonNull
    @ColumnInfo(name="valor_mult")
    var valorMult: String,

    @NonNull
    @ColumnInfo(name="usr_crea")
    var usrCrea: String,

    @NonNull
    @ColumnInfo(name="fec_crea")
    var fecCrea: String,

    @NonNull
    @ColumnInfo(name="valor")
    var valor: String,

    @NonNull
    @ColumnInfo(name="critico")
    var critico: Boolean,

    @ColumnInfo(name="answer")
    var answer: TypeAnswer?= TypeAnswer.NN,
    ){
    fun toMapper(
    ): NewCheckListQuestion {
        return NewCheckListQuestion(
            descripcion= this.descripcion,
            nombre = this.nombre,
            tipo = this.tipo,
            critico = this.critico,
            valor = this.valor,
            orden = this.orden,
            iD_CHECK = this.idCheck,
            feccrea = this.fecCrea,
            grupo = "",
            iD_CHECKDET = this.idCheckDet,
            iD_CHECKGROUP = this.idCheckGroup,
            iD_TIPO = this.idTipo,
            nombrecheckgroup = this.nombreCheckGroup,
            pregunta = "",
            reqfoto = this.regFoto,
            tipopregunta = "",
            usrcrea = this.usrCrea,
            valormult = this.valorMult,
            answer = this.answer
        )
    }
}