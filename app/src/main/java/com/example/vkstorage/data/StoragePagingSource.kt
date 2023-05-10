package com.example.vkstorage.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.vkstorage.common.util.toMyFile
import com.example.vkstorage.domain.model.MyFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StoragePagingSource(private val directory: File?, private val order: Comparator<MyFile>, private val filesInitial: List<File> = emptyList()) : PagingSource<Int, MyFile>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyFile> {
        val pageNumber = if (filesInitial.size < params.loadSize && filesInitial.isNotEmpty()) 0 else params.key ?: 0
        val pageSize = params.loadSize

        val files = withContext(Dispatchers.IO) {
            directory?.listFiles()?.map { it.toMyFile() }?.sortedWith(order)?.toList() ?: if (filesInitial.isNotEmpty()) filesInitial.map { it.toMyFile() }.sortedWith(order) else emptyList()
        }

        val startIndex = pageNumber * pageSize
        val endIndex = startIndex + pageSize

        val page = buildList<MyFile> {
            for (file in files.subList(startIndex.coerceAtMost(files.size), endIndex.coerceAtMost(files.size))) {
                add(MyFile(file.absolutePath))
            }
        }

        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (page.size == pageSize) pageNumber + 1 else null

        return LoadResult.Page(page, prevKey, nextKey)
    }

    override fun getRefreshKey(state: PagingState<Int, MyFile>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
