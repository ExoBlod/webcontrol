package com.webcontrol.android.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 100
    }

    override fun onReceive(context: Context, intent: Intent) {
        SyncService.enqueueWork(context, Intent())
    }
}
