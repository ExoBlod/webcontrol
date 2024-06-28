package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.db.entity.CheckListTest
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import com.webcontrol.angloamerican.ui.preaccessmine.ui.newPreaccesMine.NewPreAccessMineFragment
import javax.inject.Inject

class GetCheckListUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(type: NewPreAccessMineFragment.Companion.ChecklistType, workerId: String, vehicleId: String, date: String): Pair<NewPreAccessMineFragment.Companion.ChecklistType, CheckListTest> {
        var checklist = CheckListTest()

        checklist = if(type== NewPreAccessMineFragment.Companion.ChecklistType.TDV){
            repository.getChecklistTDV(
                type.name, workerId, vehicleId,date
            )
        } else{
            repository.getChecklist(
                type.name, workerId, vehicleId, date
            )
        }

        return Pair(type, checklist)
    }
}