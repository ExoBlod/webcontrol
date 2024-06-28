package com.webcontrol.android.ui.videocall

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.webcontrol.android.databinding.ActivityIncomingCallBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class IncomingCallActivity : AppCompatActivity() {
    lateinit var vibrator : Vibrator
    lateinit var context : Context
    lateinit var notification : Uri
    lateinit var player :MediaPlayer
    lateinit var EMISOR : String
    var flagSound: Boolean = true
    var flagVibrate : Boolean = true
    var rejected : Boolean = false
    var incoming : Boolean = false
    val builder = NotificationBuilder.initializeWithDefaults()
    private lateinit var binding: ActivityIncomingCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("WCIncomingCall: ", "Ingresó a actividad")
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_incoming_call)
        EMISOR= intent.getStringExtra("asunto")!!
        binding.txtLlamadaEntrante.text="Videollamada Entrante"
        if(EMISOR.isNullOrBlank())
            EMISOR="de Webcontrol"
        binding.txtCallFrom.text=EMISOR
        context=applicationContext;
        vibrator= context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        notification=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        setMediaPlayerDataSource()
        displayNotification(true,"Videollamada Entrante")
        incoming=true
        Timer("SettingUp", false).schedule(60000) {
            Log.i("WCIncomingCall: ", "rejected: "+rejected)
            if(!rejected)
                displayNotification(true, "Videollamada Perdida")
            stopVibrate()
            finish()
        }
        binding.includevideocall.acceptVideoCall.setOnClickListener{
            stopVibrate()
            displayNotification(false, "Videollamada Entrante")
            val intent = Intent(this@IncomingCallActivity, VideoCallActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.includevideocall.dismissVideoCall.setOnClickListener {
            displayNotification(false, "Videollamada Perdida")
            displayNotification(true, "Videollamada Perdida")
            rejected=true
            stopVibrate()
            val intent = Intent(this@IncomingCallActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        vibrate()
    }

    override fun onDestroy() {
        super.onDestroy()
        displayNotification(false, "void")
    }

    fun displayNotification(flagNotify : Boolean, titulo : String)
    {
        val notification = builder
                .buildNotification(applicationContext, "VideoLlamada entrante0", titulo, EMISOR, incoming)
        if(flagNotify)
            builder.showNotification(this.applicationContext, 100, notification, "notification_title")
        else
            builder.HideNotification(100);
    }


    fun vibrate()
    {
        var audio : AudioManager= context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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

    fun setMediaPlayerDataSource()
    {
        //var file : File= File(fileInfo)
        //var inputStream : FileInputStream= FileInputStream(file)
        //player.reset()
        player = MediaPlayer()
        player.isLooping = true
        player.setAudioStreamType(AudioManager.STREAM_RING)
        player.setDataSource(context,notification)
        player.prepare()
        //inputStream.close()
    }
}
