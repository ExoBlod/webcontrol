package com.webcontrol.android.ui.videocall

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import com.webcontrol.android.util.NotificationBuilder
import java.io.Serializable
import java.util.*
import kotlin.concurrent.schedule

class CallAlarmManager() : Parcelable {
    var cnt : Context? = null
    lateinit var vibrator : Vibrator //= cnt?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var notification : Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    lateinit var player :MediaPlayer
    var flagSound: Boolean = true
    var EMISOR : String="" //= emisor
    var flagVibrate : Boolean = true
    val builder = NotificationBuilder.initializeWithDefaults()

    constructor(parcel: Parcel) : this() {
        notification = parcel.readParcelable(Uri::class.java.classLoader)!!
        flagSound = parcel.readByte() != 0.toByte()
        EMISOR = parcel.readString().toString()
        flagVibrate = parcel.readByte() != 0.toByte()
    }


    fun Start() {
        setMediaPlayerDataSource()
        Timer("SettingUp", false).schedule(60000) {
            displayNotification(true, "Videollamada Perdida")
            stopVibrate()
        }
        vibrate()
        //displayNotification(true,"Videollamada Entrante")
    }

    fun Stop() {
        stopVibrate()
    }

    fun setMediaPlayerDataSource() {
        //var file : File= File(fileInfo)
        //var inputStream : FileInputStream= FileInputStream(file)
        //player.reset()
        player = MediaPlayer()
        player.isLooping = true
        player.setAudioStreamType(AudioManager.STREAM_RING)
        player.setDataSource(cnt!!,notification)
        player.prepare()
        //inputStream.close()
    }

    fun displayNotification(flagNotify : Boolean, titulo : String) {
        val notification = builder
                .buildNotification(cnt, "VideoLlamada entrante0", titulo, EMISOR, false)
        if(flagNotify)
            builder.showNotification(this.cnt!!, 100, notification, "notification_title")
        else
            builder.HideNotification(100);
    }

    fun vibrate() {
        var audio : AudioManager= cnt!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = cnt!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        when(audio.ringerMode)
        {
            AudioManager.RINGER_MODE_NORMAL ->{
                player.start()
                var pattern : LongArray= longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000)
                if(Build.VERSION.SDK_INT>=26)
                {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else
                    vibrator.vibrate(pattern, 17)
            }
            AudioManager.RINGER_MODE_SILENT->{
                flagSound=false
                flagVibrate=false
            }
            AudioManager.RINGER_MODE_VIBRATE->{
                player.stop()
                flagSound=false
                var pattern : LongArray= longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000)
                if(Build.VERSION.SDK_INT>=26)
                {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else
                    vibrator.vibrate(pattern, 17)
            }

        }
    }

    fun stopVibrate(){
        if(flagSound)
            player.stop()
        if(flagVibrate)
            vibrator.cancel()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(notification, flags)
        parcel.writeByte(if (flagSound) 1 else 0)
        parcel.writeString(EMISOR)
        parcel.writeByte(if (flagVibrate) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CallAlarmManager> {
        override fun createFromParcel(parcel: Parcel): CallAlarmManager {
            return CallAlarmManager(parcel)
        }

        override fun newArray(size: Int): Array<CallAlarmManager?> {
            return arrayOfNulls(size)
        }
    }


}