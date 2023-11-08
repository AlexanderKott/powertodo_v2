package site.kotty_kov.powertodo.todolist.main.inprogress

import site.kotty_kov.powertodo.todolist.main.common.Values
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object TimeCalculator {

      fun shortTimerGetTime(obj: Values.TimerDataStorage, time : Long) =
         time  + Values.arraySpinnerValues[obj.timeSpinnerPosition] * Values.TIME_UNIT

    /// ----- Long timer calcs
    private  fun getNearestDoW(today: Int, days: Array<Boolean>, includeToday: Boolean): Int {
        val offset = 1 //days array offset
        var marker = today

        //Если искомый день совпал с сегоднящним и он нужен (includeToday), то вернуть его
        if (days[marker - offset] && includeToday) {
            return marker //today
        }

        //ищи дальше на этой неделе или со следующей, начиная с завтра (today это marker + 1)
        marker = if (today == 7) {
            0
        } else {
            today
        }  //искомый день начиная с завтра

        val todayoffset = today - offset //перевести код today в позицию в массиве

        while (marker != todayoffset) {
            if (days[marker]) {
                break
            }
            marker++

            if (marker >= days.size) {
                marker = 0
            }
        }

        if (marker == todayoffset && days[marker]) return -(marker + offset) //Нашел. mark for next week
        if (marker == todayoffset) return -100 //cannot find
        return marker + offset  //Нашел. this week

    }

    private fun isTimeAfter(time: String, currentTime: Date): Boolean {
        val format = SimpleDateFormat("HH:mm")
        val timeToChek = format.parse(time)
        return format.format(timeToChek) > format.format(currentTime)
    }

    /**
     * addOffset=false - включит сегодняшную дату в поиск
     */
   private fun getDateOfDowByCode(
        time: String,
        calendar: Calendar,
        dayToFind: Int,
        today: Int,
        addOffset: Boolean
    ): Long {
        val offset = if (addOffset) 1 else 0

        val ptime = time.split(":").map { it.toInt() }
        calendar.set(Calendar.HOUR_OF_DAY, ptime[0]);
        calendar.set(Calendar.MINUTE, ptime[1]);
        calendar.add(Calendar.DATE, ((7 + dayToFind - (today + offset)) % 7) + offset)
        return calendar.timeInMillis
    }

    fun longTimerGetTime(time: String, days: Array<Boolean>, calendar: Calendar): Long {
        val fromToday = isTimeAfter(time, Date( calendar.timeInMillis))
        val today = calendar[Calendar.DAY_OF_WEEK]

        return when (val nearestDayCode = getNearestDoW(today, days, fromToday)) {
            -100 -> -1 // "cannot find"
            //next week
            in Int.MIN_VALUE..0 ->   getDateOfDowByCode(time, calendar, abs(nearestDayCode), today, true)
            //this week
            else -> getDateOfDowByCode(time, calendar, nearestDayCode, today, false)
        }
    }

}