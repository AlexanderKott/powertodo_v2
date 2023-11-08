package site.kotty_kov.powertodo.todolist.main.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InProgressListTableDAO {
    @Query("SELECT * FROM inProgress_table WHERE color =:color ORDER BY number DESC")
    fun getAllInProgressRecords(color : Int):  LiveData<List<InProgressItem>>

    @Query("SELECT * FROM inProgress_table WHERE number =:id")
    fun getRecordById(id : Long):   InProgressItem


    @Query("SELECT * FROM inProgress_table")
    fun getAllInProgressRecords():  List<InProgressItem>


    @Insert
    fun insert(t: InProgressItem)

    @Update
    fun update(t: InProgressItem)

    @Delete
    fun delete(t: InProgressItem)
}