package site.kotty_kov.powertodo.todolist.main.data.timer

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem


@Entity
data class ScheduledTimer(
    @PrimaryKey(autoGenerate = true)
     val id: Int = 1,
    @Embedded  val item: InProgressItem,
    val minimalInterval : Long = Long.MAX_VALUE
)