package site.kotty_kov.powertodo.todolist.main.inprogress

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.common.App
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.repository.RoomRepositoryImpl
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TimeChangedBroadcastReceiver : BroadcastReceiver() {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val roomRepo = RoomRepositoryImpl(App.getApp().getDatabase())


    override fun onReceive(context: Context, intent: Intent) {
        executor.execute(Runnable {
            var proceedNitification  = false

            roomRepo.getAllInProgressRecords().forEach {
                if (it.timerSet) {
                    proceedNitification =true
                    return@forEach
                }
            }

            if (proceedNitification){
                if (intent.action.equals("android.intent.action.TIME_SET" , ignoreCase = true)) {
                    notifyByEvent(context, "timeChangedNotify")
                    return@Runnable
                }

                if (intent.action.equals("android.intent.action.TIMEZONE_CHANGED", ignoreCase = true)) {
                    notifyByEvent(context,"timezoneNotify")
                    return@Runnable
                }

                if (intent.action.equals("android.intent.action.DATE_CHANGED", ignoreCase = true)) {
                    notifyByEvent(context, "dateChangedNotify")
                    return@Runnable
                }

            }

            })

        }





  private fun notifyByEvent(context : Context, shKey : String){
        val shPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        if (shPref.getBoolean(shKey, false)) {
            notifyUser(context)
        }
        return


    }

   private fun notifyUser(context: Context) {
        issueNotify(
            context,
            context.getString(R.string.twaschanged),
            context.getString(R.string.stillprop),
            Values.timersWereRestKey
        )
    }


}