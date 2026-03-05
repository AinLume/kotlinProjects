package com.example.prac11

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import androidx.core.content.edit
import androidx.core.net.toUri

class ReminderViewModel : ViewModel() {
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled

    private val _nextReminderText = MutableStateFlow("Напоминание выключено")
    val nextReminderText: StateFlow<String> = _nextReminderText

    fun init(context: Context) {
        val prefs = getPrefs(context)

        _isEnabled.value = prefs.getBoolean(KEY_ENABLED, false)
        if (_isEnabled.value) {
            _nextReminderText.value = getNextReminderText()
        }
    }

    fun enableReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        }

        scheduleAlarm(context)
        saveEnabled(context, true)
        _isEnabled.value = true
        _nextReminderText.value = getNextReminderText()
    }

    fun disableReminder(context: Context) {
        cancelAlarm(context)
        saveEnabled(context, false)
        _isEnabled.value = false
        _nextReminderText.value = "Напоминание выключено"
    }

    // Вычисляем когда следующие 20:00 — сегодня или завтра
    private fun getNextReminderText(): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return if (now.before(target)) {
            "Следующее напоминание: сегодня в 20:00"
        } else {
            "Следующее напоминание: завтра в 20:00"
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = getPendingIntent(context)

        val triggerTime = getNextTriggerTime()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    private fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context))
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getNextTriggerTime(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Если 20:00 уже прошло сегодня — ставим на завтра
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun saveEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit { putBoolean(KEY_ENABLED, enabled) }
    }

    companion object {
        const val PREFS_NAME = "reminder_prefs"
        const val KEY_ENABLED = "is_enabled"
        const val REQUEST_CODE = 1001
    }
}