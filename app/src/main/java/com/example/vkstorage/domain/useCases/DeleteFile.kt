package com.example.vkstorage.domain.useCases

import com.example.vkstorage.domain.model.MyFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteFile {

    suspend operator fun invoke(file: MyFile) {
        withContext(Dispatchers.IO) {
            file.delete()
        }
    }
}
