package com.example.vkstorage.data.dataSource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vkstorage.domain.model.FileHash

@Dao
interface FileHashDao {

    @Query("SELECT * FROM file")
    fun getHashes(): List<FileHash>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHash(fileHash: FileHash)

    @Delete
    suspend fun deleteHash(fileHash: FileHash)
}
