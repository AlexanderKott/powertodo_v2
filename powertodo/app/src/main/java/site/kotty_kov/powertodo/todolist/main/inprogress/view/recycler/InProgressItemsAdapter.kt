package site.kotty_kov.powertodo.todolist.main.todo.view.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import site.kotty_kov.powertodo.databinding.InprogressItemLayoutCardBinding
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem


class InProgressItemsAdapter(private val actionsListener: InProgressActionsListener) :
    ListAdapter<InProgressItem, InProgressItemsViewHolder>(DIFF_CALLBACK) {


    fun getItemById(id : Int): InProgressItem {
       return getItem(id)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InProgressItemsViewHolder {
        val binding =
            InprogressItemLayoutCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InProgressItemsViewHolder(binding, actionsListener)
    }

    override fun onBindViewHolder(holder: InProgressItemsViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<InProgressItem?> =
            object : DiffUtil.ItemCallback<InProgressItem?>() {
                override fun areItemsTheSame(oldItem: InProgressItem, newItem: InProgressItem): Boolean {
                    return oldItem.number == newItem.number
                }

                override fun areContentsTheSame(oldItem: InProgressItem, newItem: InProgressItem): Boolean {
                    return oldItem == newItem
                }
            }
    }


}