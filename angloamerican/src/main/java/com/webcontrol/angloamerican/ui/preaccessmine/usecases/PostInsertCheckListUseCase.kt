package com.webcontrol.angloamerican.ui.preaccessmine.usecases

import com.webcontrol.angloamerican.data.db.entity.CheckListTest
import com.webcontrol.angloamerican.data.repositories.IPreaccessMineRepository
import javax.inject.Inject

class PostInsertCheckListUseCase @Inject constructor(private val repository: IPreaccessMineRepository) {
    suspend operator fun invoke(checkListTest: CheckListTest) = repository.postChecklist(checkListTest)
}