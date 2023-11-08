package site.kotty_kov.powertodo.todolist.main.repository

import androidx.lifecycle.LiveData
import site.kotty_kov.powertodo.todolist.main.data.DoneItem
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import site.kotty_kov.powertodo.todolist.main.data.TodoItem
import site.kotty_kov.powertodo.todolist.main.data.notepad.NoteItem
import site.kotty_kov.powertodo.todolist.main.data.timer.ScheduledTimer
import site.kotty_kov.powertodo.todolist.main.data.user.UserInfo
import site.kotty_kov.powertodo.todolist.main.done.DoneRecord
import site.kotty_kov.powertodo.todolist.main.todo.view.ToDoItemTransfer

interface SharedPrefsRepository {

    fun saveSettings(whiteB: Boolean, redB: Boolean, blueB: Boolean, greenB: Boolean)
    fun getSelectedColor(): Int
}


interface UserInf {
    suspend fun getUserInfo(): UserInfo
    fun getUserInf(): LiveData<UserInfo>
    fun insertUserInfo(ui: UserInfo)
    fun updateUserInfo(ui: UserInfo)
}


interface GetInfo {
    fun getAllToDoRecords(color: Int): LiveData<List<TodoItem>>
    fun getAllInProgressRecords(color: Int): LiveData<List<InProgressItem>>
    fun getAllInProgressRecords(): List<InProgressItem>
    fun getAllDoneRecords(color: Int): LiveData<List<DoneItem>>
}

interface ToDoItemAccess {
    fun addNewItems(itemz: ToDoItemTransfer)
    fun deleteItem(item: TodoItem)
    fun updateItem(item: ToDoItemTransfer)
    fun updateItem(item: TodoItem)
    fun clearDoneItemsFromToDo(color: Int, time: Long)
    fun scheduleItem(item: TodoItem)
}

interface DoneItemAccess {
    fun deleteDoneItem(item: DoneRecord)
    fun scheduleDoneItem(item: DoneRecord)
}

interface DoneProgressItemAccess {
    fun markDoneItem(item: InProgressItem, result: Boolean, time: Long)
    fun updateItem(item: InProgressItem)
    fun getRecordById(number: Long): InProgressItem
}

interface ScheduledTimerAccess {
    fun getLastScheduledTimer(): ScheduledTimer
    fun putLastScheduledTimer(scheduledTimer: ScheduledTimer)
    fun clearLastScheduledTimer()
}

interface NotesAccess {
    fun getAllNotes(): LiveData<List<NoteItem>>
    fun upsertItem(item: NoteItem)
    fun deleteItem(item: NoteItem)
}


interface RoomRepository : NotesAccess, ScheduledTimerAccess, UserInf, GetInfo, ToDoItemAccess,
    DoneItemAccess, DoneProgressItemAccess {
    fun shutdown()
}