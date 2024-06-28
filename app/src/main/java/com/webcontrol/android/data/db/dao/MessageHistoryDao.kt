package com.webcontrol.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.webcontrol.android.data.db.entity.MessageHistory

@Dao
interface MessageHistoryDao {
    @Insert
    fun insert(record: MessageHistory?)
}