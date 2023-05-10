package com.example.vkstorage.domain.useCases

data class FileUseCases(
    val getFiles: GetFiles,
    val deleteFile: DeleteFile,
    val saveAllFilesHashes: SaveAllFilesHashes
)
