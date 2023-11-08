package site.kotty_kov.powertodo.todolist.main.done

import site.kotty_kov.powertodo.todolist.main.data.DoneItem
import java.text.SimpleDateFormat


data class DoneRecord(val id: Long, val date: Long, val record: DoneItem?)


fun prepareItems(lst: List<DoneItem>) : List<DoneRecord> {
    var ids: Long = 0
    var dateToPlace : Long = -1
    val records = ArrayList<DoneRecord>()
    val dateFormat = SimpleDateFormat("dd/MMM/yyyy")

    //плашка записей нет
    if (lst.isEmpty()) {
        records.add(DoneRecord(ids++, dateToPlace, null))
        return records
    }


   //Создать список из плашек времени и самих записей
    for (i in lst) {
        if (dateFormat.format(i.date).compareTo(dateFormat.format(dateToPlace)) != 0) {
            dateToPlace = i.date
            records.add(DoneRecord(ids++, dateToPlace, null))
        }
        records.add(DoneRecord(ids++, dateToPlace, i))
    }
    return records
}