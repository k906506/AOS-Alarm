package com.example.alarm

import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    private val onOffButton: Button by lazy {
        findViewById(R.id.onOffButton)
    }

    private val changeTimeButton: Button by lazy {
        findViewById(R.id.changeAlarmButton)
    }

    private val ampmTextView: TextView by lazy {
        findViewById(R.id.ampmTextView)
    }

    private val timeTextView: TextView by lazy {
        findViewById(R.id.timeTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initOnOffButton()
        initChangeTimeButton()
        val model = fetchDataFromSharedPreferences()
        renderView(model)
    }

    private fun initOnOffButton() {
        onOffButton.setOnClickListener {

        }
    }

    private fun initChangeTimeButton() {
        changeTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance() // 현재 시각을 가져오는 메소드
            TimePickerDialog(this, { picker, hour, minute ->
                val model = savedAlarmModel(hour, minute, false)
                renderView(model)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MILLISECOND), false).show()
        }
    }

    private fun savedAlarmModel(hour: Int, minute: Int, onOff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = false
        )

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }

        return model
    }

    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "12:00") ?: "12:00"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")
        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            ALARM_REQUEST_CODE,
//            Intent(this, AlarmReceiver::class.java),
//            PendingIntent.FLAG_NO_CREATE
//        )
//        if ((pendingIntent == null) && alarmModel.onOff) {
//            alarmModel.onOff = false
//        } else if ((pendingIntent != null) && alarmModel.onOff.not()) {
//            pendingIntent.cancel() // 알람이 종료
//        }
        return alarmModel
    }

    private fun renderView(model: AlarmDisplayModel) {
        ampmTextView.apply {
            text = model.ampmText
        }

        timeTextView.apply {
            text = model.timeText
        }

        onOffButton.apply {
            text = model.onOffText
            tag = model
        }
    }

    companion object {
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_REQUEST_CODE = 1000
    }
}