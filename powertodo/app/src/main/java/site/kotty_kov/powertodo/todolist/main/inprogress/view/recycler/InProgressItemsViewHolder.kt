package site.kotty_kov.powertodo.todolist.main.todo.view.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import site.kotty_kov.powertodo.databinding.InprogressItemLayoutCardBinding
import site.kotty_kov.powertodo.todolist.main.common.utils.ColorUtils
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class InProgressItemsViewHolder(
    private val binding: InprogressItemLayoutCardBinding,
    private val actionsListener: InProgressActionsListener
) : RecyclerView.ViewHolder(binding.root) {


 fun bind (item: InProgressItem) {
     with(binding) {
         val text: TextView = todotext
         val date: TextView = tododate
         val number: TextView = number
         timerCh.isChecked = item.timerSet
         text.setText(item.text)
         val currentDate = Date(item.date)
         val df: DateFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss")
         date.setText(df.format(currentDate))
         number.setText(StringBuilder("#").append(item.number).toString())
         cardView.background.setTint(ColorUtils.parse(item.color))

         cardView.setOnLongClickListener(View.OnLongClickListener { _ ->
             actionsListener.cardViewLongClick(item)
             true
         })

         timerCh.setOnClickListener(View.OnClickListener { view: View? ->
             actionsListener.setTimerCheckboxClick(item)
         })
     }
 }


}
