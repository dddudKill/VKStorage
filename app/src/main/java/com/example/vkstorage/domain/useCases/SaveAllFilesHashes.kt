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
            var firstLaunch = true
            val filesToInsert = mutableListOf<FileHash>()
            val recentlyChangedList = buildList<File> {
                withContext(Dispatchers.IO) {
                    val savedFiles = repository.getHashes().associateBy { it.path }.toMutableMap()
                    firstLaunch = savedFiles.isEmpty()
                    val files = getAllFiles(File(Constants.BASE_PATH))
                    for (file in files) {
                        val currentPath = file.absolutePath
                        val currentHash = calculateHash(file)
                        if (savedFiles.containsKey(currentPath)) {
                            val savedHash = savedFiles[currentPath]!!.hash
                            if (currentHash != savedHash) {
                                val fileHash = FileHash(
                                    hash = currentHash,
                                    path = currentPath
                                )
                                filesToInsert.add(fileHash)
                                add(File(currentPath))
                            }
                        } else {
                            val fileHash = FileHash(
                                hash = currentHash,
                                path = currentPath
                            )
                            filesToInsert.add(fileHash)
                            add(File(currentPath))
                        }
                    }
                }
            }
            if (firstLaunch) {
                emit(Resource.Success<List<File>>(emptyList()))
            } else {
                emit(Resource.Success<List<File>>(recentlyChangedList))
            }
            withContext(Dispatchers.IO) {
                for (file in filesToInsert) {
                    repository.insertHash(file)
                }
            }
        } catch (e: IOException) {
            emit(
                Resource.Error<List<File>>(
                    "Ошибка: ${e.message}"
                )
            )
        }
    }
}
