package com.example.alarm

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {
    private val onOffButton: Button by lazy {
        findViewById(R.id.onOffButton)
    }

    private val changeTimeButton: Button by lazy {
        findViewById(R.id.changeAlarmButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // step0 뷰를 초기화해주기

        // step1 데이터 가져오기

        // step2 뷰에 데이터 그려주기
    }

    private fun initOnOffButton() {
        onOffButton.setOnClickListener {

        }
    }

    private fun initChangeTimeButton() {
        changeTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance() // 현재 시각을 가져오는 메소드
            TimePickerDialog(this, { picker, hour, minute ->
                savedAlarmModel(hour, minute, false)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MILLISECOND), false).show()
        }
    }

    private fun savedAlarmModel(hour: Int, minute: Int, onOff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = false
        )

        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("alarm", model.makeDataForDB())
            putBoolean("onOff", model.onOff)
            commit()
        }

        return model
    }
}