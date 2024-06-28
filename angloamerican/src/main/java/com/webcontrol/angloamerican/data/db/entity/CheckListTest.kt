package com.webcontrol.angloamerican.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "CheckListTest")
class CheckListTest : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "IdDb")
    var idDb: Int = 0

    @SerializedName(value = "ChecklistId", alternate = ["checklistId"])
    @ColumnInfo(name = "IdTest")
    var idTest: Int = 0

    @SerializedName(value = "ChecklistTypeId", alternate = ["checklistTypeId"])
    @ColumnInfo(name = "TipoTest")
    var tipoTest: String? = ""

    @SerializedName(value = "ChecklistName", alternate = ["checklistName"])
    @ColumnInfo(name = "Titulo")
    var titulo: String? = ""

    @SerializedName(value = "ChecklistPeriodic", alternate = ["checklistPeriodic"])
    @ColumnInfo(name = "Periodic")
    var periodic: String? = ""

    @ColumnInfo(name = "WorkerId")
    var workerId: String? = ""

    @ColumnInfo(name = "VehicleId")
    var vehicleId: String? = ""

    @ColumnInfo(name = "DivisionId")
    var divisionId: String? = ""

    @ColumnInfo(name = "DivisionName")
    var divisionName: String? = ""

    @ColumnInfo(name = "LocalName")
    var localName: String? = ""

    @ColumnInfo(name = "LocalId")
    var localId: String? = ""

    @ColumnInfo(name = "Ost")
    var ost: String? = ""

    @ColumnInfo(name = "CompanyId")
    var companyId: String? = ""

    @SerializedName(value = "Location", alternate = ["location"])
    @ColumnInfo(name = "Location")
    var location: String? = ""

    @SerializedName(value = "ChecklistDate", alternate = ["checklistDate"])
    @ColumnInfo(name = "FechaSubmit")
    var fechaSubmit: String? = ""

    @SerializedName(value = "ChecklistTime", alternate = ["checklistTime"])
    @ColumnInfo(name = "HoraSubmit")
    var horaSubmit: String? = ""

    //@SerializedName(value = "email", alternate = {"email"})
    @Ignore
    var email: String? = ""

    /* private Boolean ChecklistRight;*/
    @ColumnInfo(name = "EstadoInterno")
    var estadoInterno: Int = 0

    @ColumnInfo(name = "Orden")
    var orden: Int = 0

    @ColumnInfo(name = "Estado")
    var estado: Int = 0

    @Ignore
    @SerializedName(value = "Groups", alternate = ["groups"])
    var grupoList: List<ChecklistGroups> = ArrayList()

    constructor() {}

    @Ignore
    constructor(name: String?, date: String?, workerId: String?, vehicleId: String?) {
        this.titulo = name
        this.fechaSubmit = date
        this.workerId = workerId
        this.vehicleId = vehicleId
    }

    @Ignore
    constructor(
        idTest: Int,
        tipoTest: String?,
        titulo: String?,
        periodic: String?,
        orden: Int,
        estado: Int,
        testList: List<ChecklistGroups>
    ) {
        this.idTest = idTest
        this.tipoTest = tipoTest
        this.titulo = titulo
        this.periodic = periodic
        this.orden = orden
        this.estado = estado
        this.grupoList = testList
    }
    fun isValidDivisionId(divisionId: String?): Boolean {
        return divisionId == "LB" || divisionId == "LT"
    }
}