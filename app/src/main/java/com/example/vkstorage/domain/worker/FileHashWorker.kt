package com.example.vkstorage.domain.worker
//
//import android.content.Context
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.vkstorage.common.Constants
//import com.example.vkstorage.common.util.calculateHash
//import com.example.vkstorage.common.util.getAllFiles
//import com.example.vkstorage.domain.model.FileHash
//import com.example.vkstorage.domain.repository.StorageRepository
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedInject
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.io.File
//
//@HiltWorker
//class FileHashWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted workerParams: WorkerParameters,
//    private val repository: StorageRepository
//) : CoroutineWorker(context, workerParams) {
//
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        val files = getAllFiles(File(Constants.BASE_PATH))
//        val savedFiles = repository.getHashes()
//        for (file in files) {
//            val currentPath = file.absolutePath
//            val currentHash = calculateHash(file)
//            val existingFileIndex = savedFiles.indexOfFirst { it.path == currentPath }
//            if (existingFileIndex != -1) {
//                if (currentHash == savedFiles[existingFileIndex].hash) {
//                    val fileHash = FileHash(
//                        hash = currentHash,
//                        path = currentPath,
//                        isModifiedSinceLastLaunch = false
//                    )
//                    repository.insertHash(fileHash)
//                    continue
//                } else {
//                    if (savedFiles[existingFileIndex].isModifiedSinceLastLaunch) {
//                        val fileHash = FileHash(
//                            hash = currentHash,
//                            path = currentPath,
//                            isModifiedSinceLastLaunch = false
//                        )
//                        repository.insertHash(fileHash)
//                        continue
//                    } else {
//                        val fileHash = FileHash(
//                            hash = currentHash,
//                            path = currentPath,
//                            isModifiedSinceLastLaunch = true
//                        )
//                        repository.insertHash(fileHash)
//                        continue
//                    }
//                }
//            } else {
//                val fileHash = FileHash(
//                    hash = currentHash,
//                    path = currentPath,
//                    isModifiedSinceLastLaunch = false
//                )
//                repository.insertHash(fileHash)
//                continue
//            }
//        }
//        Result.success()
//    }
//}
