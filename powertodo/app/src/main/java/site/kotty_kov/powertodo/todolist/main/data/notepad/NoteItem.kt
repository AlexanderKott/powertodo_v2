package site.kotty_kov.powertodo.todolist.main.data.notepad

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    var text: String? = "",

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    var undoHistory: String? = null
)

