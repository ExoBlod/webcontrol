package com.webcontrol.android.data.db.dao

import androidx.room.*
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.RegControl
import kotlinx.coroutines.flow.Flow

@Dao
interface PreaccesoDetalleDao {
    @Query("select * from preacceso_detalle where vehiculo!='S' and preacceso_id= :preaccesoId order by id desc")
    fun getAllByPreaccesoId(preaccesoId: Int): List<PreaccesoDetalle>

    @Query("select * from preacceso_detalle where vehiculo!='S' and preacceso_id= :preaccessId order by id desc")
    fun getAllByPreaccessId(preaccessId: Int): Flow<List<PreaccesoDetalle>>

    @Query("select * from preacceso_detalle where id = :id")
    fun getOne(id: Int): PreaccesoDetalle?

    @Insert
    fun insert(preaccesoDetalle: PreaccesoDetalle?)

    @Insert
    suspend fun insertTemp(preaccessDetail: PreaccesoDetalle)

    @Update
    fun update(preaccesoDetalle: PreaccesoDetalle?)

    @Delete
    fun delete(preaccesoDetalle: PreaccesoDetalle?)

    @Query("select pd.rut as workerId, p.fecha as date, pd.hora as time, p.patente as vehicleId, " +
            "pd.companiaId as companyId, p.division as divisionId, p.local as localId, p.sentido" +
            " as inOut, ifnull(pd.ost,'') as ost, ifnull(pd.centroCosto,'') as costCenter, ifnull(pd.tipoPase,'') as passKind, " +
            "p.conductor as userId, p.viaje as tripId,p.passengers as passengers, pd.vehiculo as driver, pd.error as errorCode,'' as idGuia ,'ANDROID' as codPda " +
            "from preacceso p inner join  preacceso_detalle pd on " +
            "p.id=pd.preacceso_id where p.id=:id")
    suspend fun getRegControlList(id: Int): List<RegControl>
}