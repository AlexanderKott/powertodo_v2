package site.kotty_kov.powertodo.todolist.main.inprogress

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import site.kotty_kov.powertodo.todolist.main.todo.view.Utils
import kotlin.random.Random


fun objFromJson(it: InProgressItem) =
    Utils.gsonParser?.fromJson(it.scheduled, Values.TimerDataStorage::class.java)

fun issueNotify(context: Context, title: String, text: String, channel: String) {
    createNotificator(context, title, text).let {
        with(NotificationManagerCompat.from(context)) {
            notify(1000, it)}
        }

}


fun createNotificator(context: Context, title: String, text: String): Notification {
    val builder = NotificationCompat.Builder(context, Values.channelId)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        .setAutoCancel(true)

    val shPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // nothing
    } else {
        when (shPref.getInt("alarmSound", 1)) {
            0 -> builder.setNotificationSilent()
            1 -> builder.setDefaults(Notification.DEFAULT_SOUND)
            2 -> builder.setSound(getSound(context, R.raw.xilo))
            3 -> builder.setSound(getSound(context, R.raw.bells))
            4 -> builder.setSound(getSound(context, R.raw.guitar))
            5 -> builder.setSound(getSound(context, R.raw.harp))
        }
    }
    return builder.build()
}





fun getSound(context: Context, resId: Int): Uri {
    val path = "android.resource://" + context.getPackageName() + "/"
    return Uri.parse(path + resId)
}