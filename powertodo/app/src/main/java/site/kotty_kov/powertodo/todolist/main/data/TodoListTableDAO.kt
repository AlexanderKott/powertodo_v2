package site.kotty_kov.powertodo.todolist.main.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoListTableDAO {
    @Query("SELECT * FROM todos_table WHERE color =:color ORDER BY number DESC")
    fun getAllRecords(color : Int):  LiveData<List<TodoItem>>

    @Query("SELECT * FROM todos_table WHERE color =:color AND done ORDER BY number DESC")
    fun getAllRecordsDone(color : Int):  List<TodoItem>

    @Insert
    fun insert(t: TodoItem)

    @Insert
    fun insert(t: List<TodoItem>): List<Long>

    @Update
    fun update(t: TodoItem)

    @Delete
    fun delete(t: TodoItem)

    @Delete
    fun delete(t: List<TodoItem>)

    @Query("DELETE FROM todos_table WHERE number = :userId")
    fun delete(userId: Long)
}