package site.kotty_kov.powertodo.todolist.main.done

import android.content.Context
import android.content.Intent
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.data.DoneItem
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import site.kotty_kov.powertodo.todolist.main.inprogress.timeToReadableFormat
import java.text.SimpleDateFormat
import java.util.*

object PlainTextSharingHandler {

    fun share(context: Context, text: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, context.getString(R.string.chooser))
        context.startActivity(shareIntent)
    }

    fun formatDoneItem(context: Context, it: DoneItem): String {
        return context.getString(
            R.string.doneItemFromat, it.id,
            SimpleDateFormat("dd MMM yyyy").format(Date(it.date)),
            it.text, if (it.status == true)
                context.getString(R.string.done) else context.getString(R.string.undone)
        ) + if (it.elapsedtime > 0) {
             context.getString(
                R.string.elapsedtime,
                timeToReadableFormat(it.elapsedtime)
            ) + "\n"
        } else ""

    }


    fun formatInprogressItem(context: Context, it: InProgressItem): String {
        return context.getString(
            R.string.InprogressItemFromat,
            it.number,
            SimpleDateFormat("dd MMM yyyy").format(Date(it.date)),
            it.text
        ) + if (it.acceptance_criteria?.trim() != "") {
            context.getString(R.string.criteria, it.acceptance_criteria)
        } else {
            ""
        }


    }

}