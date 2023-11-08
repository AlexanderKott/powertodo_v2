package site.kotty_kov.powertodo.todolist.main.data.notepad

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesTableDAO {
    @Query("SELECT * FROM notes_table  ORDER BY id DESC")
    fun getAllNotes():  LiveData<List<NoteItem>>


    @Transaction
    fun upsert(i: NoteItem){
       if (i.id == 0L){
           insert(i)
       } else {
           update(i)
       }
    }

    @Insert
    fun insert(i: NoteItem)

    @Update
    fun update(i: NoteItem)

    @Delete
    fun delete(t: NoteItem)

}