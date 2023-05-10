package com.example.vkstorage.domain.model

import com.example.vkstorage.common.util.calculateFileSize
import java.io.File

class MyFile(filePath: String) : File(filePath) {
    var totalSize = this.calculateFileSize()
}
