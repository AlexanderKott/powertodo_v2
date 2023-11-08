package site.kotty_kov.powertodo.todolist.main.notepad.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.NotesItemLayoutCardBinding
import site.kotty_kov.powertodo.todolist.main.data.notepad.NoteItem
import site.kotty_kov.powertodo.todolist.main.notepad.NotepadActionsListener

const val size: Int = 100

class NotepadItemsViewHolder(
    private val binding: NotesItemLayoutCardBinding,
    private val actionsListener: NotepadActionsListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteItem) {
        with(binding) {
            val text: TextView = todotext
            val number: TextView = number
            text.setText(prepareText(item.text))
            number.setText("# ${item.id}")

            cardView.setOnLongClickListener(View.OnLongClickListener { _ ->
                actionsListener.cardViewLongClick(item)
                true
            })

        }
    }


    private fun prepareText(input: String?): String {
        if (input == null || input.trim() == "") {
            return binding.root.context.getString(R.string.emptynote) + "\n"
        }

        val str = input.split("\n").filter { it.trim() != "" }
            .joinToString("\n")

        val stringsObject = getLastEnterChar(str)
        val additionalEnters = when (stringsObject.entercout) {
            0, 1 -> "\n"
            else -> ""
        }

        if (stringsObject.chars < str.length) {
            return str.take(stringsObject.chars)
        } else {
            return str.take(size) + additionalEnters
        }


    }

    data class StrItemTransfer(val chars: Int, val entercout: Int)

    private fun getLastEnterChar(input: String): StrItemTransfer {
        var counter = 0
        for (i in input.indices) {
            if (input[i] == '\n') {
                counter++
                if (counter == 3 && i < size) {
                    return StrItemTransfer(i, counter)
                }
            }
        }
        return StrItemTransfer(input.length, counter)
    }

}
