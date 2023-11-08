package site.kotty_kov.powertodo.todolist.main.data.timer

import androidx.room.*

@Dao
interface ScheduledTimerTableDAO {

    @Query("SELECT * FROM ScheduledTimer")
    fun getItem(): ScheduledTimer


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: ScheduledTimer)


    @Query("DELETE FROM ScheduledTimer")
    fun delete()
}