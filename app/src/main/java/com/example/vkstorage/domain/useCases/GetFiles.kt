package com.example.vkstorage.domain.useCases

import androidx.paging.PagingData
import com.example.vkstorage.common.util.FileOrder
import com.example.vkstorage.common.util.OrderType
import com.example.vkstorage.domain.model.MyFile
import com.example.vkstorage.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class GetFiles(
    private val repository: StorageRepository
) {

    operator fun invoke(
        directory: File?,
        fileOrder: FileOrder = FileOrder.Title(OrderType.Ascending),
        files: List<File>
    ): Flow<PagingData<MyFile>> {
        val order: Comparator<MyFile> = when (fileOrder.orderType) {
            is OrderType.Ascending -> {
                when (fileOrder) {
                    is FileOrder.Title -> compareBy { it.name.lowercase() }
                    is FileOrder.Size -> compareBy { it.totalSize }
                    is FileOrder.Date -> compareBy { it.lastModified() }
                    is FileOrder.Extension -> compareBy { it.extension }
                }
            }
            is OrderType.Descending -> {
                when (fileOrder) {
                    is FileOrder.Title -> compareByDescending { it.name.lowercase() }
                    is FileOrder.Size -> compareByDescending { it.totalSize }
                    is FileOrder.Date -> compareByDescending { it.lastModified() }
                    is FileOrder.Extension -> compareByDescending { it.extension }
                }
            }
        }
        return repository.getFiles(directory, order, files)
    }
}
