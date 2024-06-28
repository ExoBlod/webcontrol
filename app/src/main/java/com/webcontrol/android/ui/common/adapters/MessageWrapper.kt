package com.webcontrol.android.ui.common.adapters

import com.webcontrol.android.data.db.entity.Message

class MessageWrapper(val message: Message) {
    var id: Int
        get() = message.id
        set(id) {
            message.id = id
        }

    var rut: String?
        get() = message.rut
        set(rut) {
            message.rut = rut
        }

    var mensaje: String
        get() = message.mensaje!!
        set(mensaje) {
            message.mensaje = mensaje
        }

    var fecha: String?
        get() = message.fecha
        set(fecha) {
            message.fecha = fecha!!
        }

    fun getFecha(tipo: Boolean): String {
        return message.fecha!!
    }

    var color: Int
        get() = message.color
        set(color) {
            message.color = color
        }

    val isImportant: Boolean
        get() = message.isImportant

    var hora: String?
        get() = message.hora
        set(hora) {
            message.hora = hora
        }

    var estado: Int
        get() = message.estado
        set(estado) {
            message.estado = estado
        }

    val isLeido: Boolean
        get() = message.isLeido

    val remitente: String?
        get() = message.remitente

    val asunto: String?
        get() = message.asunto
}