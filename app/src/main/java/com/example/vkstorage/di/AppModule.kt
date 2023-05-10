package com.example.vkstorage.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.vkstorage.data.dataSource.FileHashDatabase
import com.example.vkstorage.data.repository.StorageRepositoryImpl
import com.example.vkstorage.domain.repository.StorageRepository
import com.example.vkstorage.domain.useCases.DeleteFile
import com.example.vkstorage.domain.useCases.FileUseCases
import com.example.vkstorage.domain.useCases.GetFiles
import com.example.vkstorage.domain.useCases.SaveAllFilesHashes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideFileUseCases(repository: StorageRepository): FileUseCases {
        return FileUseCases(
            getFiles = GetFiles(repository),
            deleteFile = DeleteFile(),
            saveAllFilesHashes = SaveAllFilesHashes(repository)
        )
    }

    @Provides
    @Singleton
    fun provideFileHashDatabase(application: Application): FileHashDatabase {
        return Room.databaseBuilder(
            application,
            FileHashDatabase::class.java,
            FileHashDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideStorageRepository(db: FileHashDatabase): StorageRepository {
        return StorageRepositoryImpl(db.fileHashDao)
    }
}
