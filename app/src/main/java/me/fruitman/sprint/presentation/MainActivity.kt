package me.fruitman.sprint.presentation

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import me.fruitman.sprint.R
import me.fruitman.sprint.databinding.ActivityMainBinding
import me.fruitman.sprint.domain.entities.Stage
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val CHANNEL_ID = "reminder_channel"
        private const val DAILY_REQUEST_CODE = 1
        private const val WEEKLY_REQUEST_CODE = 2
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    inner class OnDragTaskListener(val stageName: String) : View.OnDragListener {
        override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
            if (dragEvent?.action == DragEvent.ACTION_DROP) {
                Log.d("DRAG_DROP_DEBUG", "Dropped on $stageName")
                viewModel.onDragToNewColumn(
                    dragEvent.clipData.getItemAt(0).text.toString().toInt(),
                    Stage.fromName(stageName) ?: return true
                )
            }

            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DRAG_DROP_DEBUG", "testing 1 2 3")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sprintFragment = SprintFragment()
        val backlogFragment = TaskListFragment().apply {
            arguments = Bundle().apply {
                putString("stage_name", Stage.Backlog.name) } }
        val doneFragment = TaskListFragment().apply {
            arguments = Bundle().apply {
                putString("stage_name", Stage.Done.name) } }

        binding.bottomNavigation.apply {
            bottomNavBacklog.apply {
                setOnDragListener(OnDragTaskListener(Stage.Backlog.name))
                setOnClickListener {
                    supportFragmentManager.commit {
                        replace(R.id.main_content, backlogFragment)
                    }
                }
            }
            bottomNavSprint.apply {
                setOnDragListener(OnDragTaskListener(Stage.ThisWeek.name))
                setOnClickListener {
                    supportFragmentManager.commit {
                        replace(R.id.main_content, sprintFragment)
                    }
                }
            }
            bottomNavDone.apply {
                setOnDragListener(OnDragTaskListener(Stage.Done.name))
                setOnClickListener {
                    supportFragmentManager.commit {
                        replace(R.id.main_content, doneFragment)
                    }
                }
            }
        }

        supportFragmentManager.commit {
            replace(R.id.main_content, sprintFragment)
        }

//        setAlarms()
    }

    private fun setAlarms() {
        registerNotificationChannel()
        setDailyNotificationAlarm()
        setWeeklyNotificationAlarm()
    }

    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminders"
            val descriptionText = "Daily and weekly reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channelId = "reminder_channel"
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setWeeklyNotificationAlarm() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val currentCalendar = Calendar.getInstance()

        // If current time is past Sunday 6 PM, move calendar to next Sunday
        if (currentCalendar.after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationBroadcastReceiver::class.java).apply {
            putExtra("title", "Time to Plan Your Week!")
            putExtra("body", "Click here to select this week's tasks")
        }
        val pendingIntent = PendingIntent.getBroadcast(this, WEEKLY_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)
        // Cancel previous alarms
        alarmManager.cancel(pendingIntent)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    private fun setDailyNotificationAlarm() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val currentCalendar = Calendar.getInstance()

        // If current time is past 6 PM, move calendar to next day
        if (currentCalendar.after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationBroadcastReceiver::class.java).apply {
            putExtra("title", "Time to Plan Your Day!")
            putExtra("body", "Click here to select today's tasks")
        }
        val pendingIntent = PendingIntent.getBroadcast(this, DAILY_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)
        // Cancel previous alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmManager.cancelAll()
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}