package com.webcontrol.angloamerican.data.db.dao

import androidx.room.*
import com.webcontrol.angloamerican.data.dto.RegControl
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import kotlinx.coroutines.flow.Flow

@Dao
interface PreaccesoDetalleMinaDao {
    @Query("select * from preacceso_detalle_mina where vehiculo!='S' and preacceso_id= :preaccesoId order by id desc")
    suspend fun getAllByPreaccesoId(preaccesoId: Int): List<PreaccesoDetalleMina>

    @Query("select * from preacceso_detalle_mina order by id desc")
    suspend fun all(): List<PreaccesoDetalleMina>

    @Query("select * from preacceso_detalle_mina where vehiculo!='S' and preacceso_id= :preaccessId order by id desc")
    suspend fun getAllByPreaccessId(preaccessId: Int): List<PreaccesoDetalleMina>

    @Query("select * from preacceso_detalle_mina where id = :id")
    suspend fun getOne(id: Int): PreaccesoDetalleMina?

    @Insert
    suspend fun insert(preaccesoDetalle: PreaccesoDetalleMina?): Long

    @Insert
    suspend fun insertTemp(preaccessDetail: PreaccesoDetalleMina)

    @Update
    suspend fun update(preaccesoDetalle: PreaccesoDetalleMina?)

    @Query("update preacceso_detalle_mina set estado = 'S' where id = :id")
    suspend fun updateStatus(id: Int)

    @Delete
    suspend fun delete(preaccesoDetalle: PreaccesoDetalleMina?)

    @Query("select pd.rut as workerId, p.fecha as date, pd.hora as time, p.patente as vehicleId, " +
            "pd.companiaId as companyId, p.division as divisionId, p.local as localId, p.sentido" +
            " as inOut, ifnull(pd.ost,'') as ost, ifnull(pd.centroCosto,'') as costCenter, ifnull(pd.tipoPase,'') as passKind, " +
            "p.conductor as userId, p.viaje as tripId,p.passengers as passengers, pd.vehiculo as driver, pd.error as errorCode,'' as idGuia ,'ANDROID' as codPda " +
            "from preacceso_mina p inner join  preacceso_detalle_mina pd on " +
            "p.id=pd.preacceso_id where p.id=:id")
    suspend fun getRegControlList(id: Int): List<RegControl>
}