package site.kotty_kov.powertodo.todolist.main.todo.view

import com.google.gson.Gson
import com.google.gson.GsonBuilder


data class ToDoItemTransfer(val id: Long, val lines: String, val elapsedTime : Int,  val multiLine: Boolean, val colorID: Int)

object Utils {
    private var gson: Gson? = null
    val gsonParser: Gson?
         get() {
            if (null == gson) {
                val builder = GsonBuilder()
                gson = builder.create()
            }
            return gson
        }
}