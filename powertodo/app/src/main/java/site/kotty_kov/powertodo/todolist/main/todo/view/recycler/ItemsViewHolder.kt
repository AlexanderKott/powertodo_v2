package site.kotty_kov.powertodo.todolist.main.todo.view.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import site.kotty_kov.powertodo.databinding.TodoItemLayoutCardBinding
import site.kotty_kov.powertodo.todolist.main.common.utils.ColorUtils
import site.kotty_kov.powertodo.todolist.main.data.TodoItem
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ItemsViewHolder(
    private val binding: TodoItemLayoutCardBinding,
    private val actionsListener: ActionsListener
) : RecyclerView.ViewHolder(binding.root) {


 fun bind (todoitem: TodoItem) {
     with(binding) {
         val text: TextView = todotext
         val date: TextView = tododate
         val number: TextView = number
         doneChb.isChecked = todoitem.done
         text.setText(todoitem.text)
         val currentDate = Date(todoitem.date )
         val df: DateFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss")
         date.setText(df.format(currentDate))
         number.setText(StringBuilder("#").append(todoitem.number).toString())
         cardView.background.setTint(ColorUtils.parse(todoitem.color))

         cardView.setOnLongClickListener(View.OnLongClickListener { _ ->
             actionsListener.cardViewLongClick(todoitem)
             true
         })


         doneChb.setOnClickListener(View.OnClickListener { view: View? ->
             todoitem.done = !todoitem.done
             actionsListener.doneCheckboxClick(todoitem)

         })

     }
 }


}
