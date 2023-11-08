package site.kotty_kov.powertodo.todolist.main.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DoneTableDAO {
    @Query("SELECT * FROM done_table WHERE color =:color ORDER BY date DESC")
    fun getAllDoneRecords(color : Int):  LiveData<List<DoneItem>>

    @Insert
    fun insert(t: DoneItem)

    @Insert
    fun insert(t: List<DoneItem>)

    @Delete
    fun delete(t: DoneItem)

}