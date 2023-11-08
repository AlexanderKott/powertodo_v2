package site.kotty_kov.powertodo.todolist.main.notepad

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import site.kotty_kov.powertodo.databinding.NotesItemLayoutCardBinding
import site.kotty_kov.powertodo.todolist.main.data.notepad.NoteItem
import site.kotty_kov.powertodo.todolist.main.notepad.recycler.NotepadItemsViewHolder


class NoteItemsAdapter(private val actionsListener: NotepadActionsListener) :
    ListAdapter<NoteItem, NotepadItemsViewHolder>(DIFF_CALLBACK) {


    fun getItemById(id : Int): NoteItem {
       return getItem(id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotepadItemsViewHolder {
        val binding =
            NotesItemLayoutCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotepadItemsViewHolder(binding, actionsListener)
    }

    override fun onBindViewHolder(holder: NotepadItemsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<NoteItem?> =
            object : DiffUtil.ItemCallback<NoteItem?>() {
                override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}