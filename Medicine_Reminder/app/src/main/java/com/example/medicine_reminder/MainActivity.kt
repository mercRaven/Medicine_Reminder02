package com.example.medicine_reminder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var setReminderButton: Button
    private lateinit var cancelReminderButton: Button
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner: Spinner = findViewById(R.id.daysSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.days_of_week,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        val spinner1: Spinner = findViewById(R.id.timeLabelSpin)
        ArrayAdapter.createFromResource(
            this,
            R.array.days_label,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner1.adapter = adapter
        }
        val spinner2: Spinner = findViewById(R.id.alarmSoundSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.alarm_sounds,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setReminderButton = findViewById(R.id.setReminderButton)
        cancelReminderButton = findViewById(R.id.cancelReminderButton)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setReminderButton.setOnClickListener {
            setAlarm()
        }

        cancelReminderButton.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun setAlarm() {
        // Get selected time from the SeekBar and Spinner
        val timeSeekBar = findViewById<SeekBar>(R.id.timeSeekBar)
        val selectedTime = timeSeekBar.progress
        val selectedDay = findViewById<Spinner>(R.id.daysSpinner).selectedItem.toString()

        // Set alarm time
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, selectedTime / 60)
            set(Calendar.MINUTE, selectedTime % 60)
            set(Calendar.SECOND, 0)
        }

        // Create an Intent to trigger the AlarmReceiver
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("DAY", selectedDay)
        }
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
    }

    private fun cancelAlarm() {
        alarmManager.cancel(alarmIntent)
        // Reset the screen to its original state
        resetScreen()
    }

    private fun resetScreen() {
        // Reset EditText fields to empty
        findViewById<EditText>(R.id.medicineNameEditText).text = null
        findViewById<EditText>(R.id.dosageEditText).text = null
        findViewById<EditText>(R.id.frequencyEditText).text = null

        // Reset SeekBar to default progress
        findViewById<SeekBar>(R.id.timeSeekBar).progress = 720 // Or whatever the default value is

        // Reset Spinners to default selections
        findViewById<Spinner>(R.id.daysSpinner).setSelection(0) // Assuming the first item is default
        findViewById<Spinner>(R.id.timeLabelSpin).setSelection(0) // Assuming the first item is default

        // Reset TextViews to default text
        findViewById<TextView>(R.id.timerTextView).text = "00:00"
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        // Retrieve the extra data from the intent
        val day = intent?.getStringExtra("DAY")

        // Handle the alarm action here, for example, show a toast message
        val message = "Alarm for $day"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

