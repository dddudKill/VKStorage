package com.example.vkstorage.data.dataSource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vkstorage.domain.model.FileHash

@Database(
    entities = [FileHash::class],
    version = 3,
    exportSchema = false
)
abstract class FileHashDatabase : RoomDatabase() {

    abstract val fileHashDao: FileHashDao

    companion object {
        const val DATABASE_NAME = "files_db"
    }
}
