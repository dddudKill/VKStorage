package com.example.vkstorage.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file")
data class FileHash(
    val hash: String,
    @PrimaryKey
    val path: String,
    val isModifiedSinceLastLaunch: Boolean = false
)
