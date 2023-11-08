package site.kotty_kov.powertodo.todolist.main.inprogress

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.common.App
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import site.kotty_kov.powertodo.todolist.main.data.timer.ScheduledTimer
import site.kotty_kov.powertodo.todolist.main.todo.view.Utils
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepository
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepositoryImpl
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class NotificationPublisher : BroadcastReceiver() {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private val roomRepo : RoomRepository = RoomRepositoryImpl(App.getApp().getDatabase())

    private var minimalIntervalForNextTimer: Long = Long.MAX_VALUE
    private var nextTimeToWakeUp: Long = 0

    private var itemToNotify: InProgressItem? = null


    override fun onReceive(context: Context, intent: Intent) {


        executor.execute(Runnable {
            //Background work here

            var timeRightNow = System.currentTimeMillis()

            var showTimersWereResetNotify = false

            //Очистить просроченные таймеры
            roomRepo.getAllInProgressRecords().forEach {
                if (it.timerSet) {
                    objFromJson(it)
                        ?.let { obj ->
                            if (obj.mode == 1 && TimeCalculator.shortTimerGetTime(
                                    obj,
                                    it.whenTimerSet
                                )
                                < timeRightNow
                            ) {
                                it.timerSet = false
                                showTimersWereResetNotify = true
                                roomRepo.updateItem(it)
                            }
                        }
                }
            }

            if (showTimersWereResetNotify) {
                issueNotify(
                    context,
                    context.getString(R.string.detectedExpiredTimers),
                    context.getString(R.string.twerereset),
                    Values.channelId
                )
            }


            // Загрузить запланированый таймер в переменную
            roomRepo.getLastScheduledTimer()?.let { it ->
                it.item?.let { item ->
                    Utils.gsonParser?.fromJson(item.scheduled, InProgressItem::class.java)
                        ?.let { obj ->
                            //юзер не успел сбросить то что было загружено?
                            roomRepo.getRecordById(it.item.number)?.let{ r ->
                                if (r.timerSet) {
                                    minimalIntervalForNextTimer = it.minimalInterval
                                    itemToNotify = it.item
                                    nextTimeToWakeUp = obj.whenTimerSet
                                }
                            }

                        }
                }
            }


            //Display notification
            itemToNotify?.let {
                if (timeRightNow >= it.whenTimerSet) {
                    issueNotify(
                        context,
                        context.getString(R.string.yourTask,it.number),
                        it.text ?: "",
                        "${it.number}"
                    )
                    objFromJson(it)?.let { obj ->
                        if (obj.mode == 1) {
                            if (!obj.repeat) {
                                it.timerSet = false
                            } else {
                                it.whenTimerSet =
                                    TimeCalculator.shortTimerGetTime(obj, it.whenTimerSet)
                            }
                        }

                        if (obj.mode == 2) {
                            val newTime = TimeCalculator.longTimerGetTime(
                                obj.time,
                                obj.days,
                                Calendar.getInstance()
                            )
                            if (newTime != -1L) it.whenTimerSet = newTime

                        }

                        //пустить на повтор
                        minimalIntervalForNextTimer = Long.MAX_VALUE
                        calcAndSetMinimalIntervalTimer(it, timeRightNow)

                    }
                    roomRepo.updateItem(it)
                }
            }


            //Вычислить что пойдет на повтор
            roomRepo.getAllInProgressRecords().forEach {
                if (it.timerSet) {
                    calcAndSetMinimalIntervalTimer(it, timeRightNow)
                }
            }


            //Повторное запланирование или его отмена
            if (nextTimeToWakeUp > 0) {
                NotificationHelper.scheduleNotification(
                    context, nextTimeToWakeUp, NotificationHelper.TServiceState.SELF_RUN
                )

                itemToNotify?.let {
                    roomRepo.putLastScheduledTimer(
                        ScheduledTimer(
                            item = it,
                            minimalInterval = minimalIntervalForNextTimer
                        )
                    )
                }

            } else {
                roomRepo.clearLastScheduledTimer()
            }



            handler.post(Runnable {
                //UI Thread work here
            })
        })


    }


    private fun calcAndSetMinimalIntervalTimer(it: InProgressItem, timeNow: Long) {
        val interval = it.whenTimerSet - timeNow
        if (interval > 0 && interval < minimalIntervalForNextTimer) {
            nextTimeToWakeUp = it.whenTimerSet
            minimalIntervalForNextTimer = interval
            itemToNotify = it
        }

    }

}