package com.example.vkstorage.presentation

import androidx.paging.PagingData
import com.example.vkstorage.common.Constants
import com.example.vkstorage.common.util.FileOrder
import com.example.vkstorage.common.util.OrderType
import com.example.vkstorage.domain.model.MyFile
import java.io.File

data class StorageStates(
    val files: PagingData<MyFile> = PagingData.empty(),
    val currentPath: File? = File(Constants.BASE_PATH),
    val previousPath: File? = null,
    val fileOrder: FileOrder = FileOrder.Title(OrderType.Ascending),
    val isOrderSectionVisible: Boolean = false,
    val isShowingRecent: Boolean = false,
    val isAbleToShowRecent: Boolean = true,
    val isLoadingRecent: Boolean = false,
    val isDeletingFile: Boolean = false,
    val recentlyModifiedFiles: List<File> = emptyList(),
    val error: String = ""
)
