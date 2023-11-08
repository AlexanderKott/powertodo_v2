package site.kotty_kov.powertodo.todolist.main.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inProgress_table")
data class InProgressItem(
    @PrimaryKey(autoGenerate = true)
    var number: Long = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    var text: String? = "",
    var acceptance_criteria: String? = "",
    var date: Long = 0,
    var scheduled: String = "",
    var color: Int = 0,
    var timerSet: Boolean = false,
    var whenTimerSet: Long = 0,
    var etime: Int


)

