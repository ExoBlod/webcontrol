package com.webcontrol.android.ui.reservabus

data class Clock(
        val time: String
) {
    private var hours: Int
    private var minutes: Int
    private var seconds: Int

    init {
        val actualTime: String = time.replace(" ", "").replace(":", "")
        this.hours = actualTime.substring(0, 2).toInt()
        this.minutes = actualTime.substring(2, 4).toInt()
        this.seconds = actualTime.substring(4, 6).toInt()
    }

    fun tick() {
        if (this.seconds == 59) {
            if (this.minutes == 59) {
                if (this.hours == 23) {
                    hours = 0
                } else {
                    hours++
                }
                minutes = 0
            } else {
                minutes++
            }
            seconds = 0
        } else {
            seconds++
        }
    }

    override fun toString(): String {
        val currentHour = String.format("%02d", hours)
        val currentMinute = String.format("%02d", minutes)
        val currentSecond = String.format("%02d", seconds)
        return "$currentHour:$currentMinute:$currentSecond"
    }
}