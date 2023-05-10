package com.example.vkstorage.domain.useCases

import com.example.vkstorage.common.Constants
import com.example.vkstorage.common.Resource
import com.example.vkstorage.common.util.calculateHash
import com.example.vkstorage.common.util.getAllFiles
import com.example.vkstorage.domain.model.FileHash
import com.example.vkstorage.domain.repository.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class SaveAllFilesHashes(
    private val repository: StorageRepository
) {
    operator fun invoke(): Flow<Resource<List<File>>> = flow {
        try {
            emit(Resource.Loading<List<File>>())
            val recentlyChangedList = buildList<File> {
                withContext(Dispatchers.IO) {
                    val files = getAllFiles(File(Constants.BASE_PATH))
                    val savedFiles = repository.getHashes()
                    for (file in files) {
                        val currentPath = file.absolutePath
                        val currentHash = calculateHash(file)
                        val existingFileIndex = savedFiles.indexOfFirst { it.path == currentPath }
                        if (existingFileIndex != -1) {
                            if (currentHash == savedFiles[existingFileIndex].hash) {
                                val fileHash = FileHash(
                                    hash = currentHash,
                                    path = currentPath,
                                    isModifiedSinceLastLaunch = false
                                )
                                repository.insertHash(fileHash)
                            } else {
                                if (savedFiles[existingFileIndex].isModifiedSinceLastLaunch) {
                                    val fileHash = FileHash(
                                        hash = currentHash,
                                        path = currentPath,
                                        isModifiedSinceLastLaunch = false
                                    )
                                    repository.insertHash(fileHash)
                                } else {
                                    val fileHash = FileHash(
                                        hash = currentHash,
                                        path = currentPath,
                                        isModifiedSinceLastLaunch = true
                                    )
                                    add(File(currentPath))
                                    repository.insertHash(fileHash)
                                }
                            }
                        } else {
                            val fileHash = FileHash(
                                hash = currentHash,
                                path = currentPath,
                                isModifiedSinceLastLaunch = true
                            )
                            add(File(currentPath))
                            repository.insertHash(fileHash)
                        }
                    }
                }
            }
            emit(Resource.Success<List<File>>(recentlyChangedList))
        } catch (e: IOException) {
            emit(
                Resource.Error<List<File>>(
                    "Ошибка: ${e.message}"
                )
            )
        }
    }
}
