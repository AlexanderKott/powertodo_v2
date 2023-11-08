package site.kotty_kov.powertodo.todolist.main.inprogress

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import site.kotty_kov.powertodo.todolist.main.common.Values


object NotificationHelper {

    enum class TServiceState { FIRST_RUN, BACKGROUND_RUN, USER_RUN, SELF_RUN }


    private fun cancelReminder(context: Context, channel: Int) {
        val intent1 = Intent(context, NotificationPublisher::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            channel, intent1, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = context.getSystemService(ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent)
        pendingIntent.cancel()
    }


    fun scheduleNotification(context: Context, delayInSeconds: Long, state: TServiceState) {
        val notificationIntent = Intent(context, NotificationPublisher::class.java)
        notificationIntent.putExtra(Values.stateKey, state.toString())

        if (state != TServiceState.SELF_RUN) {
            cancelReminder(context, Values.reminderKey)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Values.reminderKey,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val futureInMillis = delayInSeconds
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                futureInMillis,
                pendingIntent
            );
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                futureInMillis,
                pendingIntent
            );
        else {
            alarmManager?.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent)
        }
    }
}