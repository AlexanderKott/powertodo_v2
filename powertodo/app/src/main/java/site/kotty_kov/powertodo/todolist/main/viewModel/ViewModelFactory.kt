package site.kotty_kov.powertodo.todolist.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import site.kotty_kov.powertodo.todolist.main.common.App
import site.kotty_kov.powertodo.todolist.main.data.db.TodoRoomDatabase


internal class ViewModelFactory(private val base: TodoRoomDatabase) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            return ToDoViewModel(base, App.getApp()) as T
        }
        if (modelClass.isAssignableFrom(NotePadViewModel::class.java)) {
            return NotePadViewModel(base, App.getApp()) as T
        }
        if (modelClass.isAssignableFrom(CommonViewModel::class.java)) {
            return CommonViewModel(base, App.getApp()) as T
        }


        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

