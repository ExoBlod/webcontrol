package com.webcontrol.android.data.services

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.util.Constants
import com.webcontrol.android.util.NotificationBuilder.Companion.initializeWithDefaults
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.needUpdate
import com.webcontrol.android.util.SharedUtils.setRoomTwilio
import com.webcontrol.android.util.SharedUtils.setToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        setToken(this, s)
        needUpdate(this, true)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //var intent: Intent? = null
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "Tag:MyLock")
        val room = remoteMessage.data["room"]
        val datetime = remoteMessage.data["datetime"]
        wl.acquire(10000)
        if (!room.isNullOrEmpty() && !datetime.isNullOrEmpty()) {
            checkDateTime(remoteMessage)
        } else {
            val builder = initializeWithDefaults()
            val notification = builder
                    .buildNotification(applicationContext, remoteMessage.data["message_id"], remoteMessage.data["title"], remoteMessage.data["body"], true)
            builder.showNotification(this.applicationContext, 100, notification, "notification_title")
        }
    }

    private fun handleInvite(notificationId: Int, asunto: String, mensaje: String) {
        val intent = Intent(this, IncomingCallNotificationService::class.java)
        intent.action = Constants.ACTION_INCOMING_CALL
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        intent.putExtra("asunto", asunto)
        intent.putExtra("mensaje", mensaje)
        startService(intent)
    }

    private fun checkDateTime(remoteMessage: RemoteMessage) {
        val room = remoteMessage.data["room"]
        val messageStringDateTime = remoteMessage.data["datetime"]!!
        val api = buildL()
        val call = api.datetime()
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
        var serverStringTime = ""
        val messageDateTime: Date? = formatter.parse(messageStringDateTime)
        var serverDateTime: Date

        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onResponse(call: Call<ApiResponseAnglo<String>>, response: Response<ApiResponseAnglo<String>>) {
                if (response.isSuccessful)
                    if (response.body()!!.isSuccess && response.body()!!.data.isNotEmpty())
                        serverStringTime = response.body()!!.data
                if (serverStringTime.isBlank())
                    Toast.makeText(applicationContext, "Sin conexion a servidor", Toast.LENGTH_LONG).show()
                else {
                    serverDateTime = formatter.parse(serverStringTime)!!
                    if (serverDateTime.after(messageDateTime)) {
                        val builder = initializeWithDefaults()
                        val notification = builder
                                .buildNotification(applicationContext, remoteMessage.data["message_id"] + " - Videollamada perdida", remoteMessage.data["title"], remoteMessage.data["body"], true)
                        builder.showNotification(applicationContext, 100, notification, "notification_title")
                    } else {
                        Log.i("WCFirebaseMessage", "Intentando abrir actividad")
                        setRoomTwilio(applicationContext, room)
                        val notificationId: Int = System.currentTimeMillis().toInt()
                        val asunto: String
                        var mensaje : String
                        if (remoteMessage.data["title"].isNullOrBlank())
                            asunto = ""
                        else
                            asunto = remoteMessage.data["title"].toString()

                        if (remoteMessage.data["body"].isNullOrBlank())
                            mensaje = ""
                        else
                            mensaje = remoteMessage.data["body"].toString()
                        handleInvite(notificationId, asunto, mensaje)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun broadcastIntent() {
        val intent = Intent()
        intent.action = "com.webcontrol.android.NEW_MESSAGE"
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}