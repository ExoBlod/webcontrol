package com.webcontrol.android.ui.newchecklist.data

data class NewCheckListQuestion(
    val descripcion: String,
    val grupo: String,
    val iD_CHECK: Int,
    val iD_CHECKDET: Int,
    val iD_CHECKGROUP: Int,
    val orden: Int,
    val iD_TIPO: String,
    val nombrecheckgroup: String,
    val pregunta: String,
    var reqfoto: String,
    val tipo: String,
    val nombre: String,
    val tipopregunta: String,
    val valormult: String,
    var usrcrea: String,
    val feccrea: String,
    var answer: TypeAnswer?=TypeAnswer.NN,
    var valor: String = "NA",
    val critico: Boolean = false
)
