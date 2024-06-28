package com.webcontrol.angloamerican.data.db.entity


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull
import java.util.*

interface TypeFactory {
    fun type(detail: CheckListTest_Detalle): Int
}

interface Visitable {
    fun type(typeFactory: TypeFactory): Int
}

@Entity(tableName = "CheckListTest_Detalle")
class CheckListTest_Detalle : Visitable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "IdDetDb")
    var idDetDb: Int = 0

    @ColumnInfo(name = "IdDb")
    var idDb: Int = 0

    @ColumnInfo(name = "group_id")
    var groupId: Int = 0

    @SerializedName(value = "QuestionId", alternate = ["questionId"])
    @ColumnInfo(name = "IdTest_Detalle")
    var idTest_Detalle = 0

    @SerializedName(value = "Title", alternate = ["title"])
    @ColumnInfo(name = "Title")
    var title: String? = ""

    @SerializedName(value = "Text", alternate = ["text"])
    @ColumnInfo(name = "Descripcion")
    var descripcion: String? = ""

    @SerializedName(value = "Checked", alternate = ["checked"])
    @ColumnInfo(name = "Checked")
    var isChecked: Boolean = false

    @ColumnInfo(name = "Estado")
    var estado: Int = 0

    @ColumnInfo(name = "Orden")
    var orden: Int = 0

    @SerializedName(value = "TestId", alternate = ["testId"])
    @ColumnInfo(name = "IdTest")
    var idTest: Int = 0

    @ColumnInfo(name = "ValorMultiple")
    @SerializedName(value = "ValorMultiple", alternate = ["valorMultiple"])
    var respuestas: String? = ""

    @ColumnInfo(name = "ValorSeleccionado")
    @SerializedName(value = "ValorSeleccionado", alternate = ["valorSeleccionado"])
    var respuestaSeleccionada: String? = ""

    @SerializedName(value = "QuestionType", alternate = ["questionType"])
    @ColumnInfo(name = "Tipo")
    var tipo: String? = ""

    @ColumnInfo(name = "ValorChecked")
    var valorChecked: String? = ""

    @ColumnInfo(name = "type_check_list")
    var typeCheckList: String? = ""

    @Ignore
    var btnSIActive: Boolean = false

    @Ignore
    var isBtnNOActive: Boolean = false

    @Ignore
    var isUnset: Boolean = true


    fun setRespuestas(respuestas: List<String>) {
        this.respuestas = respuestas.joinToString()
    }


    constructor() {}

    @Ignore
    constructor(
        idTest_Detalle: Int,
        tipo: String?,
        idTest: Int,
        title: String?,
        description: String?,
        estado: Int,
        orden: Int,
        btnSIActive: Boolean,
        btnNOActive: Boolean,
        respuestas: String
    ) {
        this.idTest_Detalle = idTest_Detalle
        this.tipo = tipo
        this.idTest = idTest
        this.title = title
        this.descripcion = description
        this.estado = estado
        this.orden = orden
        this.btnSIActive = btnSIActive
        this.isBtnNOActive = btnNOActive
        this.respuestas = respuestas
        this.typeCheckList = ""
    }

    fun getRespuestasConvert(): List<String>? {
        var items: List<String>? = ArrayList()
        if (respuestas != null) {
            items = Arrays.asList(*respuestas!!.split(",").dropLastWhile { it.isEmpty() }
                .toTypedArray())
        }
        return items
    }

    fun getRespuestaSeleccionadaConvert(): List<String>? {
        var items: List<String>? = ArrayList()
        if (respuestaSeleccionada != null) {
            items = LinkedList(
                Arrays.asList(
                    *respuestaSeleccionada!!.split(",").dropLastWhile { it.isEmpty() }
                        .toTypedArray()))
        }
        return items
    }

    fun setRespuestaSeleccionada(respuestas: List<String>) {
        var respuesta: String? = ""
        for (i in respuestas.indices) {
            if (i != 0) respuesta += ","
            respuesta += respuestas[i]
        }
        this.respuestaSeleccionada = respuesta
    }

    @Override
    override fun type(@NotNull typeFactory: TypeFactory): Int {
        return typeFactory.type(this)
    }
}