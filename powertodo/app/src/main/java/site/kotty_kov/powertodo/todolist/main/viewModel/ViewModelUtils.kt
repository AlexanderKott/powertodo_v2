package site.kotty_kov.powertodo.todolist.main.viewModel

import site.kotty_kov.powertodo.todolist.main.data.DoneItem
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import site.kotty_kov.powertodo.todolist.main.data.TodoItem
import site.kotty_kov.powertodo.todolist.main.todo.view.ToDoItemTransfer
import java.util.ArrayList

object ViewModelUtils {

    fun convertToDoItemToInProgressItem(todoItem: TodoItem): InProgressItem {
        return InProgressItem(
            number = todoItem.number,
            color = todoItem.color,
            text = todoItem.text,
            date = todoItem.date,
            etime = todoItem.elapsedtime
        )
    }

    fun prepareToDoItems(toDoItemTransferObj: ToDoItemTransfer): List<TodoItem> {
        return if (toDoItemTransferObj.multiLine) {
            val arr: List<String> = toDoItemTransferObj.lines.split("\n")

            val itemsToAdd: MutableList<TodoItem> = ArrayList<TodoItem>()
            for (i in arr.indices.reversed()) {
                if (arr[i].trim() == "") {
                    continue
                }
                val it = TodoItem(
                    text = arr[i],
                    date = System.currentTimeMillis(),
                    color = toDoItemTransferObj.colorID,
                    elapsedtime = toDoItemTransferObj.elapsedTime
                )
                itemsToAdd.add(it)
            }
            itemsToAdd
        } else { //add new or Edit
            val it = TodoItem(
                text = toDoItemTransferObj.lines,
                date = System.currentTimeMillis(),
                number = toDoItemTransferObj.id,
                color = toDoItemTransferObj.colorID,
                elapsedtime = toDoItemTransferObj.elapsedTime,
            )
            val itemsToAdd: MutableList<TodoItem> = ArrayList<TodoItem>()
            if (toDoItemTransferObj.lines.trim() != "") {
                itemsToAdd.add(it)
            }
            itemsToAdd
        }
    }

    fun convertInProgressItemToDoneItem(
        item: InProgressItem,
        result: Boolean,
        time: Long
    ): DoneItem {
        return DoneItem(
            text = item.text, date = time, status = result, id = item.number,
            acceptance_criteria = item.acceptance_criteria, color = item.color,
            elapsedtime = item.etime
        )
    }

    fun convertDoneItemToDoItemTo(item: DoneItem, time: Long): TodoItem {
        return TodoItem(
            number = 0, text = item.text,
            done = false, color = item.color,
            acceptanceCriteria = item.acceptance_criteria,
            date = time,
            elapsedtime = item.elapsedtime
        )
    }
}