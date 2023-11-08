package site.kotty_kov.powertodo.todolist.main.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import site.kotty_kov.powertodo.todolist.main.data.*
import site.kotty_kov.powertodo.todolist.main.data.notepad.NoteItem
import site.kotty_kov.powertodo.todolist.main.data.notepad.NotesTableDAO
import site.kotty_kov.powertodo.todolist.main.data.timer.ScheduledTimer
import site.kotty_kov.powertodo.todolist.main.data.timer.ScheduledTimerTableDAO
import site.kotty_kov.powertodo.todolist.main.data.user.UserDAO
import site.kotty_kov.powertodo.todolist.main.data.user.UserInfo

@Database(
    entities = [
        TodoItem::class, InProgressItem::class,
        ScheduledTimer::class, DoneItem::class,
        NoteItem::class, UserInfo::class
    ], version = 16
)

abstract class TodoRoomDatabase : RoomDatabase() {
    abstract fun todoListTableDAO(): TodoListTableDAO
    abstract fun inProgressListTableDAO(): InProgressListTableDAO
    abstract fun scheduledTimerTableDAO(): ScheduledTimerTableDAO
    abstract fun doneTableDAO(): DoneTableDAO
    abstract fun notesTableDAO(): NotesTableDAO
    abstract fun userInfoDAO(): UserDAO
}