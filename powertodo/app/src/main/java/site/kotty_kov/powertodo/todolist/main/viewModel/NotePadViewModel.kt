package site.kotty_kov.powertodo.todolist.main.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import site.kotty_kov.powertodo.todolist.main.common.App
import site.kotty_kov.powertodo.todolist.main.data.notepad.NoteItem
import site.kotty_kov.powertodo.todolist.main.data.db.TodoRoomDatabase
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepository
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepositoryImpl


class NotePadViewModel(base: TodoRoomDatabase, application: App) :
    AndroidViewModel(application) {

    private val roomRepo : RoomRepository = RoomRepositoryImpl(base)
    val notesItemsCount = MutableLiveData<Int>(0)
    val notesItems : LiveData<List<NoteItem>> = roomRepo.getAllNotes()


    val blankNote = NoteItem(0,"")
    var tempNote : NoteItem = blankNote

    //------------------------------NOTES------------------------------------------------

    fun setToDoItemsCount(itemsInRecyclerCount: Int) {
        if (itemsInRecyclerCount > 0) {
            notesItemsCount.value = 0
        } else {
            notesItemsCount.value = 1
        }
    }


    fun markRecordForEdit(item: NoteItem) {
        tempNote = item
    }



    fun getRecordForEdit() : NoteItem {
        return tempNote
    }

  //  ------------ db---------------------------



    fun deleteItem(item: NoteItem) {
        roomRepo.deleteItem(item)
    }

    fun upsert(item: NoteItem) {
        roomRepo.upsertItem(item)
        tempNote = blankNote
    }



    ///---------------------------------------------
    override fun onCleared() {
        roomRepo.shutdown()
    }




}