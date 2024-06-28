package com.webcontrol.android.data.db.entity

import androidx.room.*

@Entity(
    tableName = "historico_mensajes",
    indices = [Index("mensaje_id")],
    foreignKeys = [ForeignKey(
        entity = Message::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("mensaje_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
class MessageHistory {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "mensaje_id")
    var messageId: Int = 0

    @ColumnInfo(name = "action_id")
    var actionId: Int = 0

    @ColumnInfo(name = "fecha")
    var date: String? = ""
}