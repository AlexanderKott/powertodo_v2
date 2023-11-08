package site.kotty_kov.powertodo.todolist.main.done

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import site.kotty_kov.powertodo.BR
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.DoneHeaderLayoutBinding
import site.kotty_kov.powertodo.databinding.DoneItemLayoutBinding
import site.kotty_kov.powertodo.todolist.main.inprogress.timeToReadableFormat
import java.text.SimpleDateFormat
import java.util.*

class DoneAdapter(private val doneItemsCallBack: DoneItemsCallBack) :
    ListAdapter<DoneRecord, DataBindingViewHolder>(object : DiffUtil.ItemCallback<DoneRecord>() {

        override fun areItemsTheSame(oldItem: DoneRecord, newItem: DoneRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DoneRecord, newItem: DoneRecord): Boolean {
            return oldItem == newItem
        }
    }) {

    fun getItemById(id: Int): DoneRecord {
        return getItem(id)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder =
        when (viewType) {
            //обычная запись
            R.layout.done_item_layout -> DoneItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).also { it.handler = ItemHandler(); }

            // заголовок - временной штамп
            R.layout.done_header_layout -> DoneHeaderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).also { it.handler = HeaderHandler(doneItemsCallBack) }


            else -> throw IllegalArgumentException("no supported item id")
        }.let {
            DataBindingViewHolder(it)
        }


    override fun onBindViewHolder(holder: DataBindingViewHolder, position: Int) {
        holder.bind(getItem(position) as DoneRecord)

    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).record == null) R.layout.done_header_layout
        else R.layout.done_item_layout
    }
}

class DataBindingViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DoneRecord) {
        binding.setVariable(BR.item, item)


        /*
         android:lines="@{lines}"
            android:maxLines="@{lines}"
            android:minLines="@{lines}"
         */



         //если это обычная запись
        item.record?.let {

            //найти сколько строчек в тексте
            var x = it.text?.count { '\n' == it } ?: 0
            //и установить для текст вью лимит строк
            //binding.setVariable(BR.lines, x + 1)
            binding.setVariable(BR.text, item.record.text +
                    if (item.record.elapsedtime > 0){
                       "\n" + binding.root.context.getString(
                           R.string.elapsedtime,
                           timeToReadableFormat(item.record.elapsedtime ))
                    } else{
                        ""
                    })

            //Установить значие в зависимости от done галочки
            var display = "#${it.id} ${binding.root.context.getString(R.string.done)}"
            if (!item.record.status) {
                display = "#${it.id} ${binding.root.context.getString(R.string.undone)}"
            }
            binding.setVariable(BR.status, display)
            binding.setVariable(BR.itemColor, item.record.color)
            binding.setVariable(BR.done, item.record.status)

        }

        //Если это заголовок
        if (item.date != -1L) { // обычный заголовок
            binding.setVariable(
                BR.hdate,
                SimpleDateFormat("dd MMM yyyy").format(Date(item.date)).toString()
            )
        } else { //для пустых списков отоборажать только плашку "пусто"
            binding.setVariable(
                BR.hdate,
                binding.root.context.getString(R.string.NothingToDisplay)
            )
        }
        binding.executePendingBindings()
    }

}

