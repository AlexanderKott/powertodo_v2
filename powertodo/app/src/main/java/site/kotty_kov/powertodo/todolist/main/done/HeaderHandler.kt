package site.kotty_kov.powertodo.todolist.main.done

import android.view.View
import java.lang.StringBuilder

/**
 * Класс для обработки кликов по заголовкам с датами
 *
 * Получает записи Done, создает список из из всех записей которые принадлежат
 * заголовку и возвращает их в читаемом формате
 */

interface DoneItemsCallBack {
    fun getAllDoneItems(): List<DoneRecord>?
}

class HeaderHandler(private val contextCallBack: DoneItemsCallBack) {

    //из XML
    fun View.click(item: DoneRecord) {
        var start: DoneRecord? = null
        val listOfDoneTasksToShare: ArrayList<DoneRecord> = ArrayList()
        val listOfDoneTasks = contextCallBack.getAllDoneItems()

        listOfDoneTasks?.let {
            if (it.size == 1) return

            for (i in listOfDoneTasks) {
                if (i.id == item.id) {
                    start = item
                    continue
                }

                if (start != null && i.record == null) {
                    break
                }
                if (start != null) {
                    listOfDoneTasksToShare.add(i)
                }
            }


            val recordsToShare = listOfDoneTasksToShare.map {
                it.record?.let { record ->
                    PlainTextSharingHandler.formatDoneItem(this.context,record)
                }
            }

            val strs: StringBuilder = StringBuilder()
              recordsToShare.forEach { strs.append(it) }
            //шарить все что получилось:
            PlainTextSharingHandler.share(this.context, strs.toString())
        }

    }
}