package com.example.vkstorage.domain.repository

import androidx.paging.PagingData
import com.example.vkstorage.domain.model.FileHash
import com.example.vkstorage.domain.model.MyFile
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    fun getFiles(directory: File?, order: Comparator<MyFile>, files: List<File> = emptyList()): Flow<PagingData<MyFile>>

    fun getHashes(): List<FileHash>

    suspend fun insertHash(fileHash: FileHash)

    suspend fun deleteHash(fileHash: FileHash)
}
