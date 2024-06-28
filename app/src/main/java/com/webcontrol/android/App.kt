package com.webcontrol.android

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.facebook.stetho.Stetho
import com.webcontrol.android.data.db.AppDataBase
import com.webcontrol.android.di.*
import com.webcontrol.android.workers.SyncMessageState
import com.webcontrol.angloamerican.di.angloAmericanModule
import com.webcontrol.core.di.coreModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
open class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        db = AppDataBase.getInstance(applicationContext)

        initWorkers()
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(coreModule + angloAmericanModule + appModule + databaseModule + networkModule + repositoryModule + presentationModule)
        }

        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)
    }

    private fun initWorkers() {
        WorkManager.initialize(
            context!!,
            Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()
        )
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "SYNC-ESTADOS",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequest.Builder(
                SyncMessageState::class.java,
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        )
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var db: AppDataBase
        private var context: Context? = null

        fun getContext(): Context? {
            return context
        }
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}
