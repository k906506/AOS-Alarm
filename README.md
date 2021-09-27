# 키워드

# 구현 목록

# 개발 과정

## 1. 기본 UI 설정하기

중간에 알림으로 설정한 시각을 보여주고 하단에 두 개의 버튼을 배치해서 알림을 온오프하고 시간을 재설정할 수 있도록 구현하려했다. 

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <View
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginHorizontal="50dp"
        android:background="@drawable/background_blackring"
        app:layout_constraintBottom_toTopOf="@id/onOffButton"
        app:layout_constraintDimensionRatio="H, 1:1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/timeTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="09:00"
        android:textSize="50sp"
        app:layout_constraintBottom_toTopOf="@+id/onOffButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed" />

    <TextView
        android:id="@+id/ampmTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="AM"
        android:textSize="25sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/timeTextView" />

    <Button
        android:id="@+id/onOffButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="30sp"
        android:text="@string/on_alarm"
        app:layout_constraintBottom_toTopOf="@+id/changeAlarmButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/changeAlarmButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="30sp"
        android:layout_marginBottom="30sp"
        android:text="@string/time_change"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

텍스트만 보여주기에는 밋밋해서 동그란 형태의 영역 내부에 시각을 표시하도록 했다. 

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="@color/white" />

    <stroke
        android:width="1dp"
        android:color="@color/black" />
    <size
        android:width="250dp"
        android:height="250dp" />
</shape>
```

`drawable` 로 `shape` 를 구현하고 `oval` 타입을 설정했다. `stroke` 를 통해 얇은 선을 추가해줬다.

## 2. 시각 설정하기

몇 시간 뒤에 알람이 울릴 것인지 세팅하는 것은 `TimePicker` 를 이용했다.

```kotlin
	private val changeTimeButton: Button by lazy {
        findViewById(R.id.changeAlarmButton)
    }
```

우선 시각을 설정하는 button 레이아웃과 연결해줬다. 이후 `TimePicker` 롤 설정해줬는데 TimePicker에 들어가는 값들은 Listener와 default로 설정할 hour와 minute, Boolean이 들어간다. 

```kotlin
	private fun initChangeTimeButton() {
        changeTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance() // 현재 시각을 가져오는 메소드
            TimePickerDialog(this, { picker, hour, minute ->
                val model = savedAlarmModel(hour, minute, false)
                renderView(model)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MILLISECOND), false).show()
        }
	    }
```

위에서 연결해준 `changeTimeButton` 이 호출되면 현재 시각을 가져온다. Calendar 클래스의 `getInstance` 를 통해 가져온다. 이후 TimePickerDialog 생성자를 통해서 호출하는데 두 번째 인자로 hour와 minute을 설정한 다음 진행하는 것을 받는다. 람다식으로 설정한 hour와 minute을 저장하고 이를 다시 렌더링해주는 함수에 넣어줬다. 세 번째와 네 번째 인자로는 TimePicker를 켰을 때 Default로 보여줄 hour와 minute를 넣어준다. 현재 시각을 넣어줬다.

### savedAlarmModel

다음으로 알아볼 것을 TimePicker에서 설정한 시각을 실제 시각으로 변경해주는 메소드인 `savedAlarmModel` 메소드이다. 우선 그 전에 알람 객체에 대한 정보를 담고 있는 데이터 클래스 `AlarmDisplayModel` 클래스를 살펴보자.

```kotlin
package com.example.alarm

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
) {
    val timeText: String
        get() {
            val h = "%02d".format(
                if (hour < 12) hour else hour - 12
            )
            val m = "%02d".format(minute)

            return "$h:$m"
        }

    val ampmText: String
        get() {
            return if (hour < 12) "AM" else "PM"
        }

    val onOffText: String
        get() {
            return if (onOff) "알람 끄기" else "알람 켜기"
        }

    fun makeDataForDB(): String {
        return "$hour:$minute"
    }
}
```

`getter` 를 통해서 특정한 형태를 갖도록 구현했다. 다시 메소드로 넘어가보자.

```kotlin
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
```

`Data` 클래스에 넘겨주어 객체를 생성한다. 이후 `SharedPreferences` 에 저장한다. `SharedPreferences` 는 하드디스크?와 비슷하다. 데이터를 저장하기 위한 공간 정도로 이해하면 된다.

![](https://images.velog.io/images/k906506/post/14ec6a35-d28e-4ae5-8a77-8fc2bc5001a2/image.png)![]

[안드로이드 공식문서 - SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences?hl=ko)

위의 사진은 공식문서에서 가져온 것이고 `key - value` 를 이용해서 값을 저장하고 불러올 수 있다. 여기에 저장할 값은 `알람으로 설정한 시각` 과 `On - Off` 에 대한 정보이다. 우선 `private` 로 공간을 생성하고 이후에 `with` 과 `edit` 을 이용해서 값을 저장한다. 객체 자체에서 바로 edit을 해서 설정하는 경우 `commit()` 메소드를 호출할 필요가 없지만 `with` 을 통해서 값을 저장하는 경우 `commit()` 을 무조건 해줘야한다. 그래야 변경된 값이 저장된다. 

```kotlin
	companion object {
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_REQUEST_CODE = 1000
    }
```

키의 경우 정적 변수로 선언해줬는데 그 이유는 변경되면 안되기 때문이다. 

## 3. 시각 가져오기

위에서 시각을 저장하는 메소드까진 완료했다. 이제 앱을 실행했을 때 저장된 공간에서 설정한 시각을 가져오면 된다.

```kotlin
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
			return alarmModel
}
```

우선 `sharedPreferences` 객체를 생성하고 `get` 을 통해 `value` 를 가져온다. 이 때 두 번째 인자로 `default` 값을 설정할 수 있다. 해당 key에 값이 없는 경우 이 default 값을 리턴하게 된다. 저장된 값을 이용해서 `AlarmDisplayModel` 객체를 만든다. 최종적으로 객체를 리턴해주는 것을 볼 수 있는데 최종적으로 이를 렌더링하는 과정을 거친다.

## 4. 시각 보여주기

```kotlin
	val model = fetchDataFromSharedPreferences()
  renderView(model)		

// ... 중략

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
```

`renderView` 메소드에선 실제로 레이아웃과 연결해주는 작업을 수행한다.
