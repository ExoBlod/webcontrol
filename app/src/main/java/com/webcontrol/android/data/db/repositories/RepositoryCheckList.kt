package com.webcontrol.android.data.db.repositories

import com.webcontrol.android.App
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.util.Constants
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.RestClient.buildAngloRx
import com.webcontrol.android.util.RestClient.buildAntaRx
import com.webcontrol.android.util.RestClient.buildBarrick
import com.webcontrol.android.util.RestClient.buildCaserones
import com.webcontrol.android.util.RestClient.buildCdl
import com.webcontrol.android.util.RestClient.buildGf
import com.webcontrol.android.util.RestClient.buildGmpRx
import com.webcontrol.android.util.RestClient.buildKinross
import com.webcontrol.android.util.RestClient.buildMc
import io.reactivex.Observable

class RepositoryCheckList {

    fun getCheckListDBByTipo(
        tipo: String,
        idCompany: String? = ""
    ): Observable<List<CheckListTest>> {
        return if (tipo == "COV")
            App.db.checkListDao().selectCheckListTestByTipoAndCompany(tipo, idCompany)
                .toObservable()
        else
            App.db.checkListDao().selectCheckListTestByTipo(tipo).toObservable()
    }

    fun getCheckListTestByTipo(
        tipo: String,
        id: String,
        idCompany: String? = ""
    ): Observable<CheckListTest> {

        when (tipo) {
            "TMZ" -> {
                return buildGmpRx().selectCheckListTest(
                    hashMapOf(
                        "ChecklistTypeId" to tipo,
                        "ChecklistId" to id
                    )
                ).map { it.data }.doOnNext {
                    var idLastInserted: Int =
                        App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                    var checkListTest: CheckListTest =
                        App.db.checkListDao().selectCheckListById(idLastInserted)!!

                    App.db.checkListDao()
                        .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                    it.grupoList.forEach { it1 ->
                        it1.preguntas.forEach { it2 ->
                            it2.idTest = it.idTest
                            it2.idDb = idLastInserted
                            it2.groupId = it1.checklistGroupId
                            App.db.checkListDao().insertCheckListTest_Detalle(it2)
                        }
                    }
                }
            }

            "COV" -> {
                when (idCompany) {
                    Companies.BR.valor -> {
                        return buildBarrick().selectCheckListTest(
                            hashMapOf(
                                "ChecklistTypeId" to tipo,
                                "ChecklistId" to id
                            )
                        ).map { it.data }.doOnNext {
                            var idLastInserted: Int =
                                App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                            var checkListTest: CheckListTest =
                                App.db.checkListDao().selectCheckListById(idLastInserted)!!

                            App.db.checkListDao()
                                .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                            it.grupoList.forEach { it1 ->
                                it1.preguntas.forEach { it2 ->
                                    it2.idTest = it.idTest
                                    it2.idDb = idLastInserted
                                    it2.groupId = it1.checklistGroupId
                                    App.db.checkListDao().insertCheckListTest_Detalle(it2)
                                }
                            }
                        }
                    }
                    Companies.GF.valor -> {
                        return buildGf().selectCheckListTest(
                            hashMapOf(
                                "ChecklistTypeId" to tipo,
                                "ChecklistId" to id
                            )
                        ).map { it.data }.doOnNext {
                            var idLastInserted: Int =
                                App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                            var checkListTest: CheckListTest =
                                App.db.checkListDao().selectCheckListById(idLastInserted)!!

                            App.db.checkListDao()
                                .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                            it.grupoList.forEach { it1 ->
                                it1.preguntas.forEach { it2 ->
                                    it2.idTest = it.idTest
                                    it2.idDb = idLastInserted
                                    it2.groupId = it1.checklistGroupId
                                    App.db.checkListDao().insertCheckListTest_Detalle(it2)
                                }
                            }
                        }
                    }
                    Companies.KRS.valor -> {
                        return buildKinross().selectCheckListTest(
                            hashMapOf(
                                "ChecklistTypeId" to tipo,
                                "ChecklistId" to id
                            )
                        ).map { it.data }.doOnNext {
                            var idLastInserted: Int =
                                App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                            var checkListTest: CheckListTest =
                                App.db.checkListDao().selectCheckListById(idLastInserted)!!

                            App.db.checkListDao()
                                .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                            it.grupoList.forEach { it1 ->
                                it1.preguntas.forEach { it2 ->
                                    it2.idTest = it.idTest
                                    it2.idDb = idLastInserted
                                    it2.groupId = it1.checklistGroupId
                                    App.db.checkListDao().insertCheckListTest_Detalle(it2)
                                }
                            }
                        }
                    }
                    Companies.CAS.valor -> {
                        return buildCaserones().selectCheckListTest(
                            hashMapOf(
                                "ChecklistTypeId" to tipo,
                                "ChecklistId" to id
                            )
                        ).map { it.data }.doOnNext {
                            var idLastInserted: Int =
                                App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                            var checkListTest: CheckListTest =
                                App.db.checkListDao().selectCheckListById(idLastInserted)!!

                            App.db.checkListDao()
                                .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                            it.grupoList.forEach { it1 ->
                                it1.preguntas.forEach { it2 ->
                                    it2.idTest = it.idTest
                                    it2.idDb = idLastInserted
                                    it2.groupId = it1.checklistGroupId
                                    App.db.checkListDao().insertCheckListTest_Detalle(it2)
                                }
                            }
                        }
                    }
                    Companies.MC.valor -> {
                        return buildMc().selectCheckListTest(
                            hashMapOf(
                                "ChecklistTypeId" to tipo,
                                "ChecklistId" to id
                            )
                        ).map { it.data }.doOnNext {
                            var idLastInserted: Int =
                                App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                            var checkListTest: CheckListTest =
                                App.db.checkListDao().selectCheckListById(idLastInserted)!!

                            App.db.checkListDao()
                                .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                            it.grupoList.forEach { it1 ->
                                it1.preguntas.forEach { it2 ->
                                    it2.idTest = it.idTest
                                    it2.idDb = idLastInserted
                                    it2.groupId = it1.checklistGroupId
                                    App.db.checkListDao().insertCheckListTest_Detalle(it2)
                                }
                            }
                        }
                    }
                    else -> {
                        return buildCdl().selectCheckListTest(
                            hashMapOf(
                                "ChecklistTypeId" to tipo,
                                "ChecklistId" to id
                            )
                        ).map { it.data }.doOnNext {
                            var idLastInserted: Int =
                                App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                            var checkListTest: CheckListTest =
                                App.db.checkListDao().selectCheckListById(idLastInserted)!!

                            App.db.checkListDao()
                                .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                            it.grupoList.forEach { it1 ->
                                it1.preguntas.forEach { it2 ->
                                    it2.idTest = it.idTest
                                    it2.idDb = idLastInserted
                                    it2.groupId = it1.checklistGroupId
                                    App.db.checkListDao().insertCheckListTest_Detalle(it2)
                                }
                            }
                        }
                    }

                }

            }

            else -> {
                return buildAngloRx().selectCheckListTest(
                    hashMapOf(
                        "ChecklistTypeId" to tipo,
                        "ChecklistId" to id
                    )
                ).map { it.data }.doOnNext {
                    var idLastInserted: Int =
                        App.db.checkListDao().getLastInsertedCheckListTestId(tipo)
                    var checkListTest: CheckListTest =
                        App.db.checkListDao().selectCheckListById(idLastInserted)!!

                    App.db.checkListDao()
                        .updateCheckListTest(checkListTest.idDb, it.idTest, it.titulo)
                    it.grupoList.forEach { it1 ->
                        it1.preguntas.forEach { it2 ->
                            it2.idTest = it.idTest
                            it2.idDb = idLastInserted
                            it2.groupId = it1.checklistGroupId
                            it2.typeCheckList = it.tipoTest
                            App.db.checkListDao().insertCheckListTest_Detalle(it2)
                        }
                    }
                }
            }

        }

    }

    fun sendCheckListTestCOV(
        checkListTest: CheckListTest,
        idCompany: String? = ""
    ): Observable<ApiResponseAnglo<Any>> {
        when (idCompany) {
            Companies.BR.valor -> {
                return buildBarrick().sendCheckListTestRespuestas(checkListTest).map { it }
            }
            Companies.GF.valor -> {
                return buildGf().sendCheckListTestRespuestas(checkListTest).map { it }
            }
            Companies.KRS.valor -> {
                return buildKinross().sendCheckListTestRespuestas(checkListTest).map { it }
            }
            Companies.CAS.valor -> {
                return buildCaserones().sendCheckListTestRespuestas(checkListTest).map { it }
            }
            Companies.MC.valor -> {
                return buildMc().sendCheckListTestRespuestas(checkListTest).map { it }
            }
            else -> {
                return buildCdl().sendCheckListTestRespuestas(checkListTest).map { it }
            }
        }
    }

    fun sendCheckListTest(checkListTest: CheckListTest): Observable<Boolean> {
        if (checkListTest.tipoTest == "TMZ") {
            return buildGmpRx().sendCheckListTestRespuestas(checkListTest).map { it.isSuccess }
        } else {
            return buildAngloRx().sendCheckListTestRespuestas(checkListTest).map { it.isSuccess }
        }
    }

    fun sendCheckListTestKrs(checkListTest: CheckListTest): Observable<Boolean> {
        return buildKinross().sendCheckListTestRespuestasTfs(checkListTest).map { it.isSuccess }
    }

    fun validatePatente(Patente: String, Division: String): Observable<ApiResponseAnglo<Vehiculo>> {
        return buildAngloRx().validatePatente(
            hashMapOf(
                "VehicleId" to Patente,
                "DivisionId" to Division
            )
        )
    }

    fun validatePatenteKrs(
        Patente: String,
        Division: String
    ): Observable<ApiResponseAnglo<Vehiculo>> {
        return buildKinross().validatePatente(
            hashMapOf(
                "VehicleId" to Patente,
                "DivisionId" to Division
            )
        )
    }

    fun getDivisiones(): Observable<ApiResponseAnglo<List<Division>>> {
        return buildAngloRx().selectDivisiones()
    }

    fun getDivisionesKrs(): Observable<ApiResponseAnglo<List<Division>>> {
        return buildKinross().selectDivisiones()
    }

    fun getLocales(division: String): Observable<ApiResponseAnglo<List<Local>>> {
        return buildAngloRx().selectLocales(
            hashMapOf(
                "DivisionId" to division,
                "LocalType" to Constants.LOCAL_TYPE_INGRESO
            )
        )
    }

    fun getLocalesKrs(division: String): Observable<ApiResponseAnglo<List<Local>>> {
        return buildKinross().selectLocales(
            hashMapOf(
                "DivisionId" to division,
                "LocalType" to Constants.LOCAL_TYPE_INGRESO
            )
        )
    }

    fun getWorker(rut: String): Observable<ApiResponseAnglo<WorkerAnglo>> {
        return buildAngloRx().getWorkerObservable(hashMapOf("WorkerId" to rut))
    }

    fun getWorkerKrs(rut: String): Observable<ApiResponseAnglo<WorkerAnglo>> {
        return buildKinross().getWorkerObservable(hashMapOf("WorkerId" to rut))
    }

    fun getWorkerByDivision(
        rut: String,
        divisionId: String
    ): Observable<ApiResponseAnglo<WorkerAnglo>> {
        return buildAngloRx().getWorkerObservable(
            hashMapOf(
                "WorkerId" to rut,
                "DivisionId" to divisionId
            )
        )
    }

    fun getWorkerByDivisionKrs(
        rut: String,
        divisionId: String
    ): Observable<ApiResponseAnglo<WorkerAnglo>> {
        return buildKinross().getWorkerObservable(
            hashMapOf(
                "WorkerId" to rut,
                "DivisionId" to divisionId
            )
        )
    }

    fun blockWorkerPass(workerPase: WorkerPase): Observable<Boolean> {
        return buildAngloRx().blockWorkerPass(workerPase).map { it.isSuccess }
    }

    fun getDatosIniciales(rut: String): Observable<DatosInicialesWorker> {
        return buildAngloRx().getDatosIniciales(rut).map { it.data }
    }

    fun updateDatosIniciales(data: DatosInicialesWorker): Observable<Boolean> {
        return buildAngloRx().updateDatosIniciales(data).map { it.isSuccess }
    }

    fun getDatosInicialesAnta(rut: String): Observable<DatosInicialesWorker> {
        return buildAntaRx().getDatosIniciales(rut).map { it.data }
    }

    fun updateDatosInicialesAnta(data: DatosInicialesWorker): Observable<Boolean> {
        return buildAntaRx().updateDatosIniciales(data).map { it.isSuccess }
    }
}