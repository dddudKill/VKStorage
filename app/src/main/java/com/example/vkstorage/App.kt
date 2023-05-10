package com.example.vkstorage

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application()/*, Configuration.Provider*/ {

//    @Inject
//    lateinit var workerFactory: HiltWorkerFactory

//    private val applicationScope = CoroutineScope(Dispatchers.Default)

//    override fun getWorkManagerConfiguration() =
//        Configuration.Builder()
//            .setWorkerFactory(workerFactory)
//            .setMinimumLoggingLevel(android.util.Log.DEBUG)
//            .build()

    override fun onCreate() {
        super.onCreate()
//        delayedInit()
    }

//    private fun delayedInit() {
//        applicationScope.launch {
//            setupRecurringWork()
//        }
//    }

//    private fun setupRecurringWork() {
//
//        val constraints = Constraints.Builder()
//            //.setRequiredNetworkType(NetworkType.UNMETERED)
//            //.setRequiresCharging(false)
//            .build()
//
//        val request = OneTimeWorkRequestBuilder<FileHashWorker>()
//            .setConstraints(constraints)
//            .build()
//
//        WorkManager.getInstance(applicationContext).enqueue(request)
//    }
}
