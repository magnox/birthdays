package com.magnox.birthdays.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.magnox.birthdays.MainActivity
import com.magnox.birthdays.R
import com.magnox.birthdays.room.PersonEntity
import java.util.*

class NotificationHandler : BroadcastReceiver() {

    companion object {
        const val INTENT_NOTIFICATION_TEXT = "INTENT_NOTIFICATION_TEXT"

        fun addOrEditBirthday(context: Context?, person: PersonEntity, isUpdate: Boolean) { //TODO add delete and update methods!
            val time = getNextBirthday(person.birthday)
            time[Calendar.HOUR_OF_DAY] = 8 //TODO make configurable
            time[Calendar.MINUTE] = 0
            setAlarm(context, time, person, isUpdate)
        }

        private fun setAlarm(context: Context?, time: Calendar, person: PersonEntity, update: Boolean) {

            if (context == null) {
                return
            }

            val intent = Intent(context, NotificationHandler::class.java)

            val notificationText = context.getString(R.string.notification_text,
                Objects.toString(person.firstName, ""),
                Objects.toString(person.lastName, ""),
                person.getAge())
            intent.putExtra(INTENT_NOTIFICATION_TEXT, notificationText)


            val flags = if (update) PendingIntent.FLAG_CANCEL_CURRENT else 0 //TODO think about FLAG_UPDATE_CURRENT?
            val pendingIntent = PendingIntent.getBroadcast(context, person.uid!!, intent, flags)

            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//            val debugTime = Calendar.getInstance() //TODO use real reminder, this is for debugging only!
//            debugTime.add(Calendar.SECOND, 5) // show notification after 5 seconds of adding

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP, debugTime.timeInMillis, pendingIntent
                    AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent
                )
            } else {
//                am.setExact(AlarmManager.RTC_WAKEUP, debugTime.timeInMillis, pendingIntent)
                am.setExact(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
            }
        }

        private fun getNextBirthday(_birthday: Calendar): Calendar {
            val birthday = Calendar.getInstance()
            val now = Calendar.getInstance()
            birthday.timeInMillis = _birthday.timeInMillis
            birthday[Calendar.YEAR] = now[Calendar.YEAR]
//            if (birthday[Calendar.DAY_OF_YEAR] < now[Calendar.DAY_OF_YEAR]) { //TODO DEBUGGING ONLY
            if (birthday[Calendar.DAY_OF_YEAR] <= now[Calendar.DAY_OF_YEAR]) {
                birthday.add(Calendar.YEAR, 1)
            }
            return birthday
        }
    }

    // triggered by alarm manager
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }

        val notificationText = intent.getStringExtra(INTENT_NOTIFICATION_TEXT) ?: return

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_event_black_24dp)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(notificationText)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true) //remove when tapped

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build()) //TODO check if ID needed!
        }
    }
}