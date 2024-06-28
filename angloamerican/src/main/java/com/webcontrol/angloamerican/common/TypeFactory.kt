package com.webcontrol.angloamerican.common

import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.CredentialCourse
import com.webcontrol.angloamerican.data.ResponseReservaBus
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import com.webcontrol.core.common.TypeFactory

class TypeFactoryCourse : TypeFactory<CredentialCourse> {
    override fun type(item: CredentialCourse): Int {
        return R.layout.row_credential_course
    }
}

class TypeFactoryBus : TypeFactory<ResponseReservaBus> {
    override fun type(item: ResponseReservaBus): Int {
        return R.layout.row_reserva_bus
    }
}

class TypeFactoryHistBus : TypeFactory<ReservaBus2> {
    override fun type(item: ReservaBus2): Int {
        return R.layout.row_historial_reserva_bus
    }
}
