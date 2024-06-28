package com.webcontrol.android.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.webcontrol.android.R
import com.webcontrol.android.ui.onboarding.LauncherActivity
import com.webcontrol.android.ui.videocall.VideoCallActivity
import java.io.Serializable

class NotificationBuilder {
    private var mDefaultChannelId: String
    private var NotifyManager: NotificationManager? = null
    private var mDefaultSoundUri: Uri
    private var mDefaultVibrationPattern: LongArray

    private constructor() {
        mDefaultChannelId = DEFAULT_CHANNEL_ID
        mDefaultSoundUri = DEFAULT_SOUND_URI
        mDefaultVibrationPattern = DEFAULT_VIBRATION_PATTERN
    }

    private constructor(params: Params) {
        mDefaultChannelId = params.mChannelId ?: DEFAULT_CHANNEL_ID
        mDefaultSoundUri = params.mSoundUri ?: DEFAULT_SOUND_URI
        mDefaultVibrationPattern = params.mVibrationPattern ?: DEFAULT_VIBRATION_PATTERN
    }

    fun buildNotification(context: Context?, messageId: String?, title: String?, message: String?, flagActivity: Boolean): Notification {
        return buildNotification(context, messageId, title, message, mDefaultChannelId
                , mDefaultSoundUri, mDefaultVibrationPattern, flagActivity)
    }

    fun buildNotification(context: Context?, messageId: String?, title: String?, message: String?
                          , channelId: String?, sound: Uri?, pattern: LongArray?, flagActivity: Boolean): Notification {
        val builder = NotificationCompat.Builder(context!!, channelId!!)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(sound)
                .setVibrate(pattern)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
        if (flagActivity) {
            val targetIntent = Intent(context, LauncherActivity::class.java)
            targetIntent.putExtra(LauncherActivity.MESSAGE_ID, messageId)

            val contentIntent : PendingIntent
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                contentIntent = PendingIntent.getActivity(
                        context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
            else{
                contentIntent = PendingIntent.getActivity(
                        context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            builder.setContentIntent(contentIntent)
        }
        return builder.build()
    }

    @JvmOverloads
    fun showNotification(
            context: Context, notificationId: Int, notification: Notification?, title: String?, broadcast: Boolean = false
    ) {
        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotifyManager = nManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel: NotificationChannel = NotificationChannel("app",
                    "app",
                    NotificationManager.IMPORTANCE_HIGH)
            nManager.createNotificationChannel(channel)
        }
        nManager.notify(notificationId, notification)
    }

    fun HideNotification(id: Int) {
        NotifyManager!!.cancel(id)
    }

    class Params(val mChannelId: String?) {
        val mSoundUri: Uri? = null
        val mVibrationPattern: LongArray? = null

    }

    companion object {
        const val DEFAULT_CHANNEL_ID = "app"
        val DEFAULT_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val DEFAULT_VIBRATION_PATTERN = longArrayOf(0, 250, 250, 250)
        var instance: NotificationBuilder? = null
            private set

        fun initializeWithDefaults(): NotificationBuilder {
            instance = NotificationBuilder()
            return instance!!
        }
    }
}