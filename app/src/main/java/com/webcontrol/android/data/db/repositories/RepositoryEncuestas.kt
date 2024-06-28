package com.webcontrol.android.data.db.repositories

import com.webcontrol.android.App
import com.webcontrol.android.data.db.entity.Encuestas
import com.webcontrol.android.data.model.*
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import io.reactivex.Observable

class RepositoryEncuestas {
    fun getEncuestasDBByTipo(tipo: String): Observable<List<Encuestas>> {
        return App.db.encuestasDao().selectEncuestasByTipo(tipo).toObservable()
    }

    fun getEncuestasTestByTipo(tipo: String, id: String,rut: String): Observable<ResponseExam> {
        return RestClient.buildAngloRx().getExamByExamId(RequestExam(id.toInt(),tipo)).map { it.data }.doOnNext {
            var idLastInserted: Int? = App.db.encuestasDao().getLastInsertedEncuestasId(tipo)
            if (idLastInserted == null){
                idLastInserted = 0
            }
            it.ExamDate = SharedUtils.wCDate
            it.ExamTime = SharedUtils.time
            it.WorkerId = rut
            App.db.encuestasDao().insertAllEncuestas(it.toEncuestas(++idLastInserted))
        }

    }

    fun sendEncuestaTest(exam: ResponseExam): Observable<Boolean> {
        return RestClient.buildAngloRx().saveExam(exam).map { it.isSuccess }

    }
}