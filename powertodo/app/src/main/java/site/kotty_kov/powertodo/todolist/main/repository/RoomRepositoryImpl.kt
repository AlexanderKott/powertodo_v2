package site.kotty_kov.powertodo.todolist.main.repository

import androidx.lifecycle.LiveData
import site.kotty_kov.powertodo.todolist.main.data.*
import site.kotty_kov.powertodo.todolist.main.data.db.TodoRoomDatabase
import site.kotty_kov.powertodo.todolist.main.data.notepad.NoteItem
import site.kotty_kov.powertodo.todolist.main.data.notepad.NotesTableDAO
import site.kotty_kov.powertodo.todolist.main.data.timer.ScheduledTimer
import site.kotty_kov.powertodo.todolist.main.data.timer.ScheduledTimerTableDAO
import site.kotty_kov.powertodo.todolist.main.data.user.UserDAO
import site.kotty_kov.powertodo.todolist.main.data.user.UserInfo
import site.kotty_kov.powertodo.todolist.main.done.DoneRecord
import site.kotty_kov.powertodo.todolist.main.todo.view.ToDoItemTransfer
import site.kotty_kov.powertodo.todolist.main.viewModel.ViewModelUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RoomRepositoryImpl(var base: TodoRoomDatabase) : RoomRepository {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var todoListDAO: TodoListTableDAO = base.todoListTableDAO()
    private var inProgressListTableDAO: InProgressListTableDAO = base.inProgressListTableDAO()
    private var scheduledTimerTableDAO: ScheduledTimerTableDAO = base.scheduledTimerTableDAO()
    private var doneTableDAO: DoneTableDAO = base.doneTableDAO()
    private var notesTableDAO: NotesTableDAO = base.notesTableDAO()

    private var userTableDAO: UserDAO = base.userInfoDAO()


    //------------------------ Password ---------------------------------------
    override suspend fun getUserInfo(): UserInfo {
        return userTableDAO.getUserObject()
    }


    override fun getUserInf(): LiveData<UserInfo> {
        return userTableDAO.getUserInfo()
    }

    override fun insertUserInfo(ui: UserInfo) {
        executor.submit { userTableDAO.insert(ui) }
    }

    override fun updateUserInfo(ui: UserInfo) {
        executor.submit { userTableDAO.update(ui) }
    }


    //----------------------------------SELECTS to LiveData

    //To do Items
    override fun getAllToDoRecords(color: Int): LiveData<List<TodoItem>> {
        return todoListDAO.getAllRecords(color)
    }

    //In progress
    override fun getAllInProgressRecords(color: Int): LiveData<List<InProgressItem>> {
        return inProgressListTableDAO.getAllInProgressRecords(color)
    }

    override fun getAllInProgressRecords(): List<InProgressItem> {
        return inProgressListTableDAO.getAllInProgressRecords()
    }


    //Done Items
    override fun getAllDoneRecords(color: Int): LiveData<List<DoneItem>> {
        return doneTableDAO.getAllDoneRecords(color)
    }


    /// -------------------------

    override fun addNewItems(itemz: ToDoItemTransfer) {
        executor.submit {
            todoListDAO.insert(ViewModelUtils.prepareToDoItems(itemz))
        }

    }

    override fun deleteItem(item: TodoItem) {
        executor.submit {
            todoListDAO.delete(item)
        }
    }

    override fun updateItem(item: ToDoItemTransfer) {
        executor.submit {
            todoListDAO.update(ViewModelUtils.prepareToDoItems(item)[0])
        }
    }

    override fun updateItem(item: TodoItem) {
        executor.submit { todoListDAO.update(item) }
    }


    override fun clearDoneItemsFromToDo(color: Int, time: Long) {
        executor.submit {
            base.runInTransaction {
                val list = todoListDAO.getAllRecordsDone(color)
                todoListDAO.delete(list)
                doneTableDAO.insert(list.map {
                    DoneItem(
                        id = it.number,
                        text = it.text, date = time, status = true, color = it.color,
                        elapsedtime = it.elapsedtime
                    )
                })
            }
        }
    }

    override fun scheduleItem(item: TodoItem) {
        executor.submit {
            base.runInTransaction {
                todoListDAO.delete(item)
                inProgressListTableDAO.insert(ViewModelUtils.convertToDoItemToInProgressItem(item))
            }
        }
    }

    //  - Mark done
    override fun markDoneItem(item: InProgressItem, result: Boolean, time: Long) {
        executor.submit {
            base.runInTransaction {
                inProgressListTableDAO.delete(item)
                doneTableDAO.insert(
                    ViewModelUtils.convertInProgressItemToDoneItem(
                        item,
                        result,
                        time
                    )
                )
            }
        }
    }

    override fun deleteDoneItem(item: DoneRecord) {
        executor.submit {
            item.record?.let { doneTableDAO.delete(it) }
        }
    }

    override fun scheduleDoneItem(item: DoneRecord) {
        executor.submit {
            item.record?.let {
                val time = System.currentTimeMillis()
                todoListDAO.insert(ViewModelUtils.convertDoneItemToDoItemTo(it, time))
            }
        }
    }


    // In Progress
    override fun updateItem(item: InProgressItem) {
        executor.submit { inProgressListTableDAO.update(item) }
    }

    override fun getRecordById(number: Long): InProgressItem {
        return inProgressListTableDAO.getRecordById(number)
    }


    //Temporal timer

    override fun getLastScheduledTimer(): ScheduledTimer {
        return scheduledTimerTableDAO.getItem()
    }

    override fun putLastScheduledTimer(scheduledTimer: ScheduledTimer) {
        scheduledTimerTableDAO.insert(scheduledTimer)
    }

    override fun clearLastScheduledTimer() {
        scheduledTimerTableDAO.delete()
    }


//---------------NOTES----------------

    override fun getAllNotes(): LiveData<List<NoteItem>> {
        return notesTableDAO.getAllNotes()
    }

    override fun upsertItem(item: NoteItem) {
        executor.submit { notesTableDAO.upsert(item) }
    }

    override fun deleteItem(item: NoteItem) {
        executor.submit { notesTableDAO.delete(item) }
    }


    // -------------------------------


    override fun shutdown() {
        executor.shutdown()
    }
    //--------------------------------


}