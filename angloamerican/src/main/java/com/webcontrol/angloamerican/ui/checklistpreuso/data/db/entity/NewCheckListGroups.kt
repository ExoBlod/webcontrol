package com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListGroup
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListQuestion


@Entity(tableName = "new_check_list_group")
class NewCheckListGroups(
    @NonNull
    @ColumnInfo(name="id")
    var id: Int =0,

    @NonNull
    @ColumnInfo(name="idCheck")
    var idCheck: Int=0,

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="checkIdGroup")
    var checkIdGroup: Int=0,

    @NonNull
    @ColumnInfo(name="check_list_type")
    var checkListType: String="",

    @NonNull
    @ColumnInfo(name="check_list_activo")
    var checkListActivo: String="",

    @NonNull
    @ColumnInfo(name="check_id_head")
    var checkIdHead: Int=0,

    @NonNull
    @ColumnInfo(name="name_check")
    var nameCheck: String="",

    @NonNull
    @ColumnInfo(name="name_group")
    var nameGroup: String=""


) {
    fun toMapper(valor:List<NewCheckListQuestion>): NewCheckListGroup {
        return NewCheckListGroup(
            idCheck = this.idCheck,
            nameCheck = this.nameCheck,
            checkListType = this.checkListType,
            checkIdHead = this.checkIdHead,
            checkIdGroup = this.checkIdGroup,
            nameGroup = this.nameGroup,
            checklistActivo = this.checkListActivo,
            data = valor,

        )
    }
}