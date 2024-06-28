package com.webcontrol.angloamerican.data.db.dao

import androidx.room.*
import com.webcontrol.angloamerican.data.db.entity.ChecklistGroups

@Dao
interface ChecklistGroupsDao {
    @Query("SELECT * FROM checklist_group WHERE idDb = :idDb AND id_tipo = :typeId AND id_check = :checkId AND id_checkgroup = :groupId")
    fun getOnebyUniqueId(idDb: Int, typeId: String?, checkId: Int, groupId: Int): ChecklistGroups?

    @get:Query("select * from checklist_group")
    val all: List<ChecklistGroups?>?

    @Query("SELECT * FROM checklist_group WHERE IdDb=:IdDb")
    fun getAllByTest(IdDb: Int): List<ChecklistGroups?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(checklistGroups: ChecklistGroups?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(checklistGroups: ChecklistGroups?)

    @Delete
    fun delete(checklistGroups: ChecklistGroups?)

    @Query("delete from checklist_group")
    fun clean()
}