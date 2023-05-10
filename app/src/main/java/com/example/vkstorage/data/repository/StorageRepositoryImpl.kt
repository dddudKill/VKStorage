package com.example.vkstorage.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.vkstorage.data.StoragePagingSource
import com.example.vkstorage.data.dataSource.FileHashDao
import com.example.vkstorage.domain.model.FileHash
import com.example.vkstorage.domain.model.MyFile
import com.example.vkstorage.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class StorageRepositoryImpl(
    private val dao: FileHashDao
) : StorageRepository {

    override fun getFiles(
        directory: File?,
        order: Comparator<MyFile>,
        files: List<File>
    ): Flow<PagingData<MyFile>> {
        return Pager(
            PagingConfig(pageSize = 20),
            pagingSourceFactory = { StoragePagingSource(directory, order, files) }
        ).flow
    }

    override fun getHashes(): List<FileHash> {
        return dao.getHashes()
    }

    override suspend fun insertHash(fileHash: FileHash) {
        dao.insertHash(fileHash)
    }

    override suspend fun deleteHash(fileHash: FileHash) {
        dao.deleteHash(fileHash)
    }
}
