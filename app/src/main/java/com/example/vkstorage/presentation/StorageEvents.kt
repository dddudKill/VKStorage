package com.example.vkstorage.presentation

import com.example.vkstorage.common.util.FileOrder
import com.example.vkstorage.domain.model.MyFile

sealed class StorageEvents {
    data class Order(val fileOrder: FileOrder) : StorageEvents()
    data class DeletingFileStarted(val file: MyFile) : StorageEvents()
    object DeleteFile : StorageEvents()
    object UndoDeletingFile : StorageEvents()
    data class OpenDirectory(val file: MyFile) : StorageEvents()
    object ToggleRecentlyChanged : StorageEvents()
    object ShowRecentlyChanged : StorageEvents()
    object GoBack : StorageEvents()
    object ToggleOrderSection : StorageEvents()
}
