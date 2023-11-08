package site.kotty_kov.powertodo.todolist.main.viewModel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import site.kotty_kov.powertodo.todolist.main.common.App
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.data.db.TodoRoomDatabase
import site.kotty_kov.powertodo.todolist.main.data.user.UserInfo
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepository
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepositoryImpl
import site.kotty_kov.powertodo.todolist.main.repository.SharedPrefsRepository
import site.kotty_kov.powertodo.todolist.main.repository.SharedPrefsRepositoryImpl


class CommonViewModel(base: TodoRoomDatabase, application: App) :
    AndroidViewModel(application) {


    //----------- data sources ---------------

    //Shared prefs
    private val colorButtons: SharedPrefsRepository =
        SharedPrefsRepositoryImpl(application.applicationContext)

    //Room
    private val roomRepo: RoomRepository = RoomRepositoryImpl(base)


    //-------------------------- variables -----------------------------------------
    //Common

    fun checkPassword() {
        viewModelScope.launch {
            val userInfo = roomRepo.getUserInfo()
              if (userInfo == null) {
                roomRepo.insertUserInfo(UserInfo(id = 0, "", Values.defaultPassword, 0))
            }
        }
    }

    fun getPassword(): LiveData<String> {
        return roomRepo.getUserInf().map { it.password }
    }

    fun isAppProtected(): LiveData<Boolean> {
        return roomRepo.getUserInf().map {
            if (it == null) {
                false
            } else {
                it.password != Values.defaultPassword
            }
        }
    }


    fun updateItem(it: UserInfo) {
        roomRepo.updateUserInfo(it)
    }


///--------------------Application states -------------------

    private var applicationDisplayState: Int = 0

    fun getApplicationState(): Int {
        return applicationDisplayState
    }

    fun setAppStateToDo() {
        applicationDisplayState = 0
    }

    fun setStateNotepad() {
        applicationDisplayState = 1
    }

    fun setPasswordLockScr() {
        applicationDisplayState = Values.LOCKED
    }

    fun setSettings() {
        applicationDisplayState = 3
    }

    fun setEditNotepadRecord() {
        applicationDisplayState = 4
    }

    fun setLastInPorgressScreenStateAsTitmer() {
        applicationDisplayState = 5
    }

    fun setLastInPorgressScreenStateAsRecordEdit() {
        applicationDisplayState = 6
    }

    fun setLastInPorgressScreenStateAsList() {
        applicationDisplayState = 7 
    }


    //---------------------------------
    override fun onCleared() {
        roomRepo.shutdown()
    }




}