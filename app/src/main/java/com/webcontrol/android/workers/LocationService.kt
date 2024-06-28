package com.webcontrol.android.workers

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.webcontrol.android.R
import com.webcontrol.android.data.model.WorkerLocation
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationService: Service() {

    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var notificationManager: NotificationManager
    lateinit var serviceHandler: Handler

    private val binder: IBinder = LocalBinder()
    private var flagConfigurationChanging = false


    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val api = RestClient.buildL()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.i(TAG, "New location: $locationResult")
                val location = locationResult.lastLocation
                val workerLocation = WorkerLocation(
                        workerId = SharedUtils.getUsuarioId(this@LocationService),
                        date = SharedUtils.wCDate,
                        time = SharedUtils.time,
                        latitude = location?.latitude.toString(),
                        longitude = location?.longitude.toString()
                )
                val call = api.sendWorkerLocation(workerLocation)
                call.enqueue(object: Callback<ApiResponseAnglo<Any>> {
                    override fun onResponse(call: Call<ApiResponseAnglo<Any>>, response: Response<ApiResponseAnglo<Any>>) {
                        Log.d("SendLocation", "response: $response")
                    }

                    override fun onFailure(call: Call<ApiResponseAnglo<Any>>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
        }
        setupLocationRequest()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()

        serviceHandler = Handler(handlerThread.looper)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val channel = NotificationChannel(LOCATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")
        val startedFromNotification = intent?.getBooleanExtra(
                EXTRA_STARTED_FROM_NOTIFICATION, false) ?: false
        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        flagConfigurationChanging = true
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i(TAG, "in onBind()")
        stopForeground(true)
        flagConfigurationChanging = false
        return binder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "in onRebind()")
        stopForeground(true)
        flagConfigurationChanging = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")
        if (!flagConfigurationChanging && SharedUtils.getRequestLocationStatus(this)) {
            Log.i(TAG, "Starting foreground service")

            startForeground(LOCATION_NOTIFICATION_ID, getNotification())
        }
        return true
    }

    override fun onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null)
    }

    inner class LocalBinder: Binder() {
        fun getService(): LocationService = this@LocationService
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_IN_MILISECONDS.toLong()
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        SharedUtils.setRequestLocationStatus(this, true)
        startService(Intent(applicationContext, LocationService::class.java))

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } catch (ex: SecurityException) {
            ex.printStackTrace()
            SharedUtils.setRequestLocationStatus(this, false)
        }
    }

    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            SharedUtils.setRequestLocationStatus(this, false)
            stopSelf()
        } catch (ex: SecurityException) {
            ex.printStackTrace()
            SharedUtils.setRequestLocationStatus(this, true)
        }
    }

    private fun getNotification(): Notification {
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

        val servicePendingIntent : PendingIntent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            servicePendingIntent = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        else{
            servicePendingIntent = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(this, LOCATION_CHANNEL_ID)
                .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                        servicePendingIntent)
                .setContentText("Webcontrol está obteniendo su ubicación")
                .setContentTitle("Webcontrol")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Webcontrol está obteniendo su ubicación")
                .setWhen(System.currentTimeMillis())

        return builder.build()
    }

    companion object {
        const val LOCATION_CHANNEL_ID = "location_channel_01"
        const val LOCATION_NOTIFICATION_ID = 12345678
        const val TAG = "LocationService"
        private const val UPDATE_INTERVAL_IN_MILISECONDS = 60000
        private const val PACKAGE_NAME = "com.webcontrol.android"
        private const val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
    }

}