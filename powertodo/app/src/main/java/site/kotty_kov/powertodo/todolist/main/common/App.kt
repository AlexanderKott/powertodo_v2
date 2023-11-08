package site.kotty_kov.powertodo.todolist.main.common

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.room.Room
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.data.db.TodoRoomDatabase


class App : Application() {
    @Volatile
    private lateinit var database: TodoRoomDatabase

    override fun onCreate() {
        super.onCreate()
        setupNotificationChannel()

        appInstance = this

        database = Room.databaseBuilder(this,
            TodoRoomDatabase::class.java,
            "database.db")
            .build()
    }

    private fun setupNotificationChannel() {
        val shPref = getSharedPreferences("Settings", MODE_PRIVATE)
        val createChanel = shPref.getBoolean("NotificationChanelDone", false)
        if (!createChanel) {
            createNotificationChanelFirstTime(this)
            shPref.edit().putBoolean("NotificationChanelDone", true).apply()
        }
    }

    fun getDatabase(): TodoRoomDatabase {
        return database
    }

    companion object {
        lateinit var appInstance: App

        @JvmStatic
        fun getApp(): App {
            return appInstance
        }
    }


    private fun createNotificationChanelFirstTime(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.alarms)
            val descriptionText = context.getString(R.string.chanalDescr)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(Values.channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                enableVibration(true)
                setSound(
                    site.kotty_kov.powertodo.todolist.main.inprogress.getSound(
                        context,
                        RingtoneManager.TYPE_NOTIFICATION
                    ), audioAttributes)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}