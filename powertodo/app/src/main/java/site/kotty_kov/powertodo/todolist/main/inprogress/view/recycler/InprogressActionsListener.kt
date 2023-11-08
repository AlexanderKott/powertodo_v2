package site.kotty_kov.powertodo.todolist.main.todo.view.recycler

import site.kotty_kov.powertodo.todolist.main.data.InProgressItem

interface InProgressActionsListener {
      fun cardViewLongClick(todoitem: InProgressItem)
      fun setTimerCheckboxClick(todoitem: InProgressItem)

}
