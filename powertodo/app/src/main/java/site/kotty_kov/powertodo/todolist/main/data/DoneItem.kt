package site.kotty_kov.powertodo.todolist.main.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "done_table")
data class DoneItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    var text: String? = "",
    var acceptance_criteria: String? = "",
    var date: Long = 0,
    var scheduled: String = "",
    var color: Int = 0,
    var status: Boolean = false,
    var elapsedtime : Int

)

