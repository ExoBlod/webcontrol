package com.webcontrol.android.workers

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

class SyncService : JobIntentService() {

    companion object {

        private const val TAG = "SyncService"
        private const val JOB_ID = 1

        fun enqueueWork(context: Context, work: Intent) {
            JobIntentService.enqueueWork(context, SyncService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "Test interval")
    }

}
