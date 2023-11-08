package site.kotty_kov.powertodo.todolist.main.todo.view.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import site.kotty_kov.powertodo.databinding.TodoItemLayoutCardBinding
import site.kotty_kov.powertodo.todolist.main.data.TodoItem


class ItemsAdapter(private val actionsListener: ActionsListener) :
    ListAdapter<TodoItem, ItemsViewHolder>(DIFF_CALLBACK) {


    fun getItemById(id : Int): TodoItem {
       return getItem(id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val binding =
            TodoItemLayoutCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemsViewHolder(binding, actionsListener)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<TodoItem?> =
            object : DiffUtil.ItemCallback<TodoItem?>() {
                override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                    return oldItem.number == newItem.number
                }

                override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}