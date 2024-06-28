package com.webcontrol.android.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.webcontrol.android.App
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.data.network.dto.SyncEstadoDto
import com.webcontrol.android.util.RestClient.buildL
import retrofit2.Call
import java.io.IOException
import java.util.*

class SyncMessageState(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        return try {
            val api = buildL()
            val mensajesEstado = App.db.messageDao().estadosNoSincronizados()
            if (mensajesEstado!!.isNotEmpty()) {
                val dtoEstados: MutableList<SyncEstadoDto?> = ArrayList()
                for (msj in mensajesEstado) {
                    dtoEstados.add(SyncEstadoDto(msj!!.id, msj.estado, msj.fechaModificacion!!, if (msj.isImportant) "1" else "0"))
                }
                val call: Call<ApiResponse<List<SyncEstadoDto>>> = api.syncStatus(dtoEstados)
                val response = call.execute()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val list = response.body()!!.data
                        if (list != null) for (est in list) {
                            App.db.messageDao().updateEstadoSync(est.id, true)
                        }
                    }
                }
            }
            val mensajesImportante = App.db.messageDao().importantesNoSincronizados()
            if (mensajesImportante!!.isNotEmpty()) {
                val dtoImportantes: MutableList<SyncEstadoDto?> = ArrayList()
                for (msj in mensajesImportante) {
                    dtoImportantes.add(SyncEstadoDto(msj!!.id, msj.estado, msj.fechaModificacion!!, if (msj.isImportant) "1" else "0"))
                }
                val call: Call<ApiResponse<List<SyncEstadoDto>>> = api.syncImportantes(dtoImportantes)
                val response = call.execute()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val list = response.body()!!.data
                        if (list != null) for (est in list) {
                            App.db.messageDao().updateImportanteSync(est.id, true)
                        }
                    }
                }
            }
            Result.success(Data.Builder().putString("resultado", "ok").build())
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure()
        }
    }
}