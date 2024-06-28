package com.webcontrol.android.data.services

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.webcontrol.android.R
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.videocall.IncomingCallActivity
import com.webcontrol.android.ui.videocall.VideoCallActivity
import com.webcontrol.android.util.Constants


class IncomingCallNotificationService : Service() {
    private val TAG: String = "WCIncomingCallService: "
    var asunto : String? =""
    var mensaje : String? =""
    //var notID : Int=0
    //private var callAlarmManager : CallAlarmManager? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action: String? = intent?.action
         asunto= intent?.getStringExtra("asunto")
         mensaje= intent?.getStringExtra("mensaje")

        if (action != null) {
            val notificationId: Int = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val h = Handler()
            val delayInMilliseconds: Long = 60000
            h.postDelayed(Runnable { notificationManager.cancel(notificationId) }, delayInMilliseconds)
            //notID=notificationId
            asunto = intent.getStringExtra("asunto")
            //callAlarmManager = intent.getParcelableExtra(Constants.CALL_ALARM_MANAGER)
            when (action) {
                Constants.ACTION_INCOMING_CALL -> handleIncomingCall(notificationId,asunto)
                Constants.ACTION_ACCEPT -> accept(notificationId)
                Constants.ACTION_REJECT -> reject(notificationId)
            }
        }
        return START_NOT_STICKY

    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handleIncomingCall(notificationId: Int, asunto: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setCallInProgressNotification(notificationId,asunto)
        }
        sendCallInviteToActivity(notificationId);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun setCallInProgressNotification(notificationId: Int, asunto: String?) {
        if (isAppVisible()) {
            Log.i(TAG, "setCallInProgressNotification - app is visible.")
            startForeground(notificationId, createNotification(notificationId, NotificationManager.IMPORTANCE_LOW, asunto))
        }else{
            Log.i(TAG, "setCallInProgressNotification - app is NOT visible.")
            startForeground(notificationId, createNotification(notificationId, NotificationManager.IMPORTANCE_HIGH, asunto))
            //callAlarmManager?.Start()
        }

    }

    private fun isAppVisible(): Boolean {
        return ProcessLifecycleOwner
                .get()
                .lifecycle
                .currentState
                .isAtLeast(Lifecycle.State.STARTED)
    }

    private fun createNotification(notificationId: Int, channelImportance: Int, asunto : String?): Notification {
        val intent = Intent(this, IncomingCallActivity::class.java)
        intent.setAction(Constants.ACTION_INCOMING_CALL_NOTIFICATION)
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent : PendingIntent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             pendingIntent =
                    PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        else{
            pendingIntent =
                    PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return buildNotification("Llamada entrante",
                    pendingIntent,
                    notificationId,
                    createChannel(channelImportance))
        }else{
            return NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Llamada entrante")
                    .setContentText(asunto)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setGroup("test_app_notification")
                    .setColor(Color.rgb(214,10,37)).build()
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun buildNotification(text: String, pendingIntent: PendingIntent,
                                  notificationId: Int, channelId: String) : Notification{
        val rejectIntent = Intent(applicationContext, IncomingCallNotificationService::class.java)
        rejectIntent.setAction(Constants.ACTION_REJECT)
        rejectIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        //rejectIntent.putExtra(Constants.CALL_ALARM_MANAGER, callAlarmManager)
        val piRejectIntent : PendingIntent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            piRejectIntent = PendingIntent.getService(applicationContext,0,rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        else{
            piRejectIntent = PendingIntent.getService(applicationContext,0,rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val acceptIntent= Intent(applicationContext, IncomingCallNotificationService::class.java)
        acceptIntent.setAction(Constants.ACTION_ACCEPT)
        acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        //acceptIntent.putExtra(Constants.CALL_ALARM_MANAGER, callAlarmManager)
        val piAcceptIntent : PendingIntent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            piAcceptIntent = PendingIntent.getService(applicationContext,0,acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        else{
            piAcceptIntent = PendingIntent.getService(applicationContext,0,acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        var pattern : LongArray= longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000)
        val builder = Notification.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(asunto + ": Videollamada Entrante")
                .setContentText(mensaje)
                .setCategory(Notification.CATEGORY_CALL)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_call_end_green_24px, "Contestar",piAcceptIntent)
                .addAction(R.drawable.ic_call_end_red_24px,"Rechazar", piRejectIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setVibrate(pattern)
                .setOngoing(true)
                .setFullScreenIntent(pendingIntent, true)

        return builder.build()


    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(channelImportance: Int) : String{
        var callInviteChannel = NotificationChannel(Constants.VOICE_CHANNEL_HIGH_IMPORTANCE,
                "Primary Video Channel", NotificationManager.IMPORTANCE_HIGH)
        var channelId = Constants.VOICE_CHANNEL_HIGH_IMPORTANCE

        if(channelImportance == NotificationManager.IMPORTANCE_LOW){
            callInviteChannel = NotificationChannel(Constants.VOICE_CHANNEL_LOW_IMPORTANCE,
                    "Primary Video Channel", NotificationManager.IMPORTANCE_LOW)
            channelId = Constants.VOICE_CHANNEL_LOW_IMPORTANCE
        }
        callInviteChannel.lightColor=Color.GREEN
        callInviteChannel.lockscreenVisibility=Notification.VISIBILITY_PRIVATE
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(callInviteChannel)


        return channelId
    }

    private fun sendCallInviteToActivity(notificationId: Int){
        if(Build.VERSION.SDK_INT >= 29 && !isAppVisible()){
            return
        }
        val intent = Intent(this, IncomingCallActivity::class.java)
        intent.setAction(Constants.ACTION_INCOMING_CALL)
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("asunto", asunto)
        this.startActivity(intent)
    }

    private fun accept(notificationId: Int){
        endForeground()
        //callAlarmManager?.Stop()
        val activeCallIntent = Intent(this, VideoCallActivity::class.java)
        activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activeCallIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        activeCallIntent.setAction(Constants.ACTION_ACCEPT);
        startActivity(activeCallIntent)
    }

    private fun reject(notificationId: Int){
        endForeground()
        /*callAlarmManager?.displayNotification(false, "Videollamada Perdida")
        callAlarmManager?.displayNotification(true, "Videollamada Perdida")
        callAlarmManager?.Stop()*/
        val activeCallIntent = Intent(this, MainActivity::class.java)
        activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activeCallIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        activeCallIntent.setAction(Constants.ACTION_REJECT);
        startActivity(activeCallIntent)
    }

    private fun endForeground(){
        stopForeground(true)
    }
}