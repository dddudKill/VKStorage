package com.example.vkstorage.common

import android.os.Environment

object Constants {
    val BASE_PATH: String = Environment.getExternalStorageDirectory().absolutePath
    const val PERMISSION_REQUEST_STORAGE = 101
}
