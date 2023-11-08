package site.kotty_kov.powertodo.todolist.main.todo.view.recycler

import site.kotty_kov.powertodo.todolist.main.data.TodoItem

interface ActionsListener {
      fun cardViewLongClick(todoitem: TodoItem)
      fun doneCheckboxClick(todoitem: TodoItem)

}
