package site.kotty_kov.powertodo.todolist.main.done

import android.view.View

class ItemHandler {
    fun View.click(item: DoneRecord) {
        item.record?.let{
            PlainTextSharingHandler.share(this.context,  PlainTextSharingHandler
                .formatDoneItem(this.context,it))
        }
    }

}