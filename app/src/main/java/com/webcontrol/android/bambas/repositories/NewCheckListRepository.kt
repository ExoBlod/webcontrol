package com.webcontrol.android.bambas.repositories

import com.webcontrol.android.data.RestInterfaceBambas
import com.webcontrol.android.data.db.dao.CheckListBambasDao
import com.webcontrol.android.data.db.entity.InspeccionVehicular
import com.webcontrol.android.ui.newchecklist.data.*
import retrofit2.http.Query

class NewCheckListRepository(private val api: RestInterfaceBambas) {
    suspend fun getWorkerHistory(workerId: String): List<NewCheckListHistory> {
        return api.getWorkerHistory(workerId)
    }

    suspend fun setSignatureWorker(workerSignature: WorkerSignature) : Boolean{
        return api.setSignatureWorker(workerSignature)
    }

    suspend fun getModelbyPlate(plateCar: String): VehicleInformation {
        return api.getModelbyPlate(plateCar)
    }

    suspend fun insertWorkerVehicleInformation(workerVehicleInformation: WorkerVehicleInformation): AnswerCheckingHead {
        return api.insertWorkerVehicleInformation(workerVehicleInformation)
    }

    suspend fun insertEvidenceInCheckDetail(requestEvidenceInsert: RequestEvidenceInsert): Boolean{
        return api.insertEvidenceInCheckDetail(requestEvidenceInsert)
    }

    suspend fun getListEvidence(workerId: String, valor: String): List<EvidenceList>{
        return api.getListEvidence(workerId, valor)
    }

    suspend fun postPhotoEvidence(newCheckListGroup: NewCheckListGroup): Boolean{
        return api.postPhotoEvidence(newCheckListGroup)
    }

    suspend fun QuestionByNo(workerId: String, IdCheck: Int): List<AnswersQuestion>{
        return api.QuestionByNo(workerId, IdCheck)
    }

    suspend fun insertEvidence(requestEvidenceInformation: RequestEvidenceInformation): Boolean{
        return api.insertEvidence(requestEvidenceInformation)
    }
    suspend fun getWorkerHistoryById(workerId: String, workerSearch: String, date: String, filter: Boolean): List<NewCheckListHistory>{
        return api.getWorkerHistoryById(workerId, workerSearch, date, filter)
    }

    suspend fun getWorkerHistoryByPlate(plate: String, workerSearch: String, date: String, filter: Boolean): List<NewCheckListHistory>{
        return api.getWorkerHistoryByPlate(plate, workerSearch, date, filter)
    }

    suspend fun questionLists(): List<NewCheckListGroup>{
        return api.questionLists()
    }

    suspend fun savingAnswer(listAnswer :List<NewCheckListGroup>): List<NewCheckListQuestion>{
        return api.savingAnswer(listAnswer)
    }

    suspend fun getListQuestionByHead(idHead: Int): List<NewCheckListGroup> {
        return api.getListQuestionByCheckingHead(idHead)
    }
}