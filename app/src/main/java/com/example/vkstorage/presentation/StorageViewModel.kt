package com.example.vkstorage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.vkstorage.common.Resource
import com.example.vkstorage.common.util.FileOrder
import com.example.vkstorage.common.util.OrderType
import com.example.vkstorage.domain.model.MyFile
import com.example.vkstorage.domain.useCases.FileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val fileUseCases: FileUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(StorageStates())
    val state: StateFlow<StorageStates> = _state

    private var getFilesJob: Job? = null

    private var recentlyDeletedFile: MyFile? = null

    init {
        getFiles(state.value.currentPath!!, FileOrder.Title(OrderType.Ascending))
        saveAllFilesHashes()
    }

    fun onEvent(event: StorageEvents) {
        when (event) {
            is StorageEvents.Order -> {
                if (state.value.fileOrder::class == event.fileOrder::class &&
                    state.value.fileOrder.orderType == event.fileOrder.orderType
                ) {
                    return
                }
                _state.value = state.value.copy(files = PagingData.empty())
                if (state.value.isShowingRecent) {
                    getFiles(null, event.fileOrder, state.value.recentlyModifiedFiles)
                    return
                }
                getFiles(state.value.currentPath!!, event.fileOrder)
            }
            is StorageEvents.OpenDirectory -> {
                _state.value = state.value.copy(
                    currentPath = event.file,
                    previousPath = event.file.parentFile,
                    files = PagingData.empty()
                )
                getFiles(state.value.currentPath!!, state.value.fileOrder)
            }
            is StorageEvents.GoBack -> {
                _state.value = state.value.copy(
                    currentPath = state.value.previousPath,
                    previousPath = state.value.previousPath?.parentFile,
                    files = PagingData.empty()
                )
                if (state.value.isShowingRecent) {
                    _state.value = state.value.copy(
                        isShowingRecent = false,
                        isAbleToShowRecent = true
                    )
                }
                getFiles(state.value.currentPath!!, state.value.fileOrder)
            }
            is StorageEvents.DeletingFileStarted -> {
                recentlyDeletedFile = event.file
                _state.value = state.value.copy(
                    isDeletingFile = true
                )
            }
            is StorageEvents.DeleteFile -> {
                viewModelScope.launch {
                    recentlyDeletedFile?.let { fileUseCases.deleteFile(it) }
                }
                recentlyDeletedFile = null
                _state.value = state.value.copy(
                    isDeletingFile = false
                )
            }
            is StorageEvents.UndoDeletingFile -> {
                recentlyDeletedFile = null
                _state.value = state.value.copy(
                    isDeletingFile = false
                )
            }
            is StorageEvents.ToggleRecentlyChanged -> {
                _state.value = state.value.copy(
                    isShowingRecent = true,
                    currentPath = null,
                    previousPath = state.value.currentPath,
                    files = PagingData.empty()
                )
            }
            is StorageEvents.ShowRecentlyChanged -> {
                if (!state.value.isShowingRecent) return
                getFiles(state.value.currentPath, state.value.fileOrder, state.value.recentlyModifiedFiles)
                _state.value = state.value.copy(
                    isAbleToShowRecent = false
                )
            }
            is StorageEvents.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getFiles(directory: File?, fileOrder: FileOrder, files: List<File> = emptyList()) {
        getFilesJob?.cancel()
        getFilesJob = fileUseCases.getFiles(directory, fileOrder, files)
            .cachedIn(viewModelScope)
            .onEach { data ->
                _state.value = state.value.copy(
                    files = data,
                    fileOrder = fileOrder
                )
            }
            .launchIn(viewModelScope)
    }

    private fun saveAllFilesHashes() {
        fileUseCases.saveAllFilesHashes().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        recentlyModifiedFiles = result.data ?: emptyList(),
                        isLoadingRecent = false,
                        error = ""
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoadingRecent = false,
                        error = result.message ?: "Ошибка"
                    )
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        isLoadingRecent = true
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}
