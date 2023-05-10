package com.example.vkstorage.domain.worker
//
//import android.content.Context
//import androidx.work.Constraints
//import androidx.work.NetworkType
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkManager
//
//class FileHashWorkerManager(context: Context) {
//
//    private val workManager = WorkManager.getInstance(context)
//
//    fun start() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
//            .setRequiresCharging(false)
//            .build()
//
//        val request = OneTimeWorkRequestBuilder<FileHashWorker>()
//            .setConstraints(constraints)
//            .build()
//
//        workManager.enqueue(
//            request
//        )
//    }
//}
