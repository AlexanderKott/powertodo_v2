package site.kotty_kov.powertodo.todolist.main.viewModel

import androidx.lifecycle.*
import site.kotty_kov.powertodo.todolist.main.common.App
import site.kotty_kov.powertodo.todolist.main.data.DoneItem
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import site.kotty_kov.powertodo.todolist.main.todo.view.ToDoItemTransfer
import site.kotty_kov.powertodo.todolist.main.data.TodoItem
import site.kotty_kov.powertodo.todolist.main.data.db.TodoRoomDatabase
import site.kotty_kov.powertodo.todolist.main.done.DoneRecord
import site.kotty_kov.powertodo.todolist.main.inprogress.NotificationHelper
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepository
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepositoryImpl
import site.kotty_kov.powertodo.todolist.main.repository.SharedPrefsRepository
import site.kotty_kov.powertodo.todolist.main.repository.SharedPrefsRepositoryImpl


class ToDoViewModel(base: TodoRoomDatabase, application: App) :
    AndroidViewModel(application) {

    init {
        NotificationHelper.scheduleNotification(
            application.applicationContext, 3000, NotificationHelper.TServiceState.FIRST_RUN
        )

    }

    //----------- data sources ---------------

    //Shared prefs
    private val colorButtons: SharedPrefsRepository =
        SharedPrefsRepositoryImpl(application.applicationContext)

    //Room
    private val roomRepo: RoomRepository = RoomRepositoryImpl(base)


    //-------------------------- variables -----------------------------------------
    //Common
    val colour: MutableLiveData<Int> = MutableLiveData<Int>(colorButtons.getSelectedColor())

    //TO DO tab
    private val _toDoItemsCount = MutableLiveData<Int>(0)
    val toDoItemsCount: MutableLiveData<Int>
        get() = _toDoItemsCount

    private val _toDoItems = colour.switchMap { color ->
        roomRepo.getAllToDoRecords(color)
    }

    val toDoItems : LiveData<List<TodoItem>>
        get() = _toDoItems


    //In Progress tab
    private val _inProgressItemsCount = MutableLiveData<Int>(0)
    val inProgressItemsCount : MutableLiveData<Int>
        get() = _inProgressItemsCount

    private val _inProgressItems = colour.switchMap { color ->
        roomRepo.getAllInProgressRecords(color)
    }
    val inProgressItems :  LiveData<List<InProgressItem>>
        get() = _inProgressItems


    //Done tab
    private val _doneItems = colour.switchMap { color ->
        roomRepo.getAllDoneRecords(color)
    }
    val doneItems :  LiveData<List<DoneItem>>
        get() = _doneItems

///--------------------Shared prefs ---------------------------------------

    private val btnArrayToSave = arrayOf(true, false, false, false)
    fun prepareButtonsStateToSave(whiteB: Boolean, redB: Boolean, blueB: Boolean, greenB: Boolean) {
        btnArrayToSave[0] = whiteB
        btnArrayToSave[1] = redB
        btnArrayToSave[2] = blueB
        btnArrayToSave[3] = greenB
    }


    fun saveColorButtonsState() {
        colorButtons.saveSettings(
            btnArrayToSave[0],
            btnArrayToSave[1],
            btnArrayToSave[2],
            btnArrayToSave[3]
        )
    }

    //--------------------------- Room -----------------------

    fun setColorToItemsReturn(colorID: Int) {
        colour.value = colorID
    }


    //----------------------------- Done Items  --------------------------------------
    fun markItemDone(item: InProgressItem, result: Boolean) {
        val time = System.currentTimeMillis()
        roomRepo.markDoneItem(item, result, time)
    }

    fun deleteDoneItem(item: DoneRecord) {
        roomRepo.deleteDoneItem(item)
    }

    fun scheduleDoneItem(item: DoneRecord) {
        roomRepo.scheduleDoneItem(item)
    }

    // -----------------------In progress --------------------------------------------
    private var tempInProgressIntem: InProgressItem? = null  //cache

    fun setInProgressItemsCount(itemsInRecyclerCount: Int) {
        if (itemsInRecyclerCount > 0) {
            inProgressItemsCount.value = 0
        } else {
            inProgressItemsCount.value = 1
        }
    }

    fun inProgressTempItemAddValue(tempValue: String) {
        tempInProgressIntem?.let {
            it.scheduled = tempValue
        }
    }

    fun markInProgressItemAsTemp(item: InProgressItem) {
        tempInProgressIntem = item
    }

    fun getInProgressTempItem(): InProgressItem? {
        return tempInProgressIntem
    }


    //------------
    fun updateInProgressItem(item: InProgressItem) {
        roomRepo.updateItem(item)
    }


    //TO DO items actions ------------------------------------------------------------------------------

    fun setToDoItemsCount(itemsInRecyclerCount: Int) {
        if (itemsInRecyclerCount > 0) {
            toDoItemsCount.value = 0
        } else {
            toDoItemsCount.value = 1
        }
    }


    fun clearDoneItems() {
        colour.value?.let {
            val time = System.currentTimeMillis()
            roomRepo.clearDoneItemsFromToDo(it, time)
        }
    }

    fun updateItem(it: ToDoItemTransfer) {
        roomRepo.updateItem(it)
    }

    fun updateItem(it: TodoItem) {
        roomRepo.updateItem(it)
    }

    fun addNewItems(trObject: ToDoItemTransfer) {
        roomRepo.addNewItems(trObject)
    }

    fun deleteItem(item: TodoItem) {
        roomRepo.deleteItem(item)
    }


    fun scheduleItem(item: TodoItem) {
        roomRepo.scheduleItem(item)
    }


    //-----------------------------------------------------------------------------------


    override fun onCleared() {
        roomRepo.shutdown()
    }


}