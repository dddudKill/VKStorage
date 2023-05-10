package com.example.vkstorage.presentation.files

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.vkstorage.databinding.StorageItemBinding
import com.example.vkstorage.domain.model.MyFile

class FileAdapter(
    private val onFileClick: (MyFile) -> Unit,
    private val onShareClick: (MyFile) -> Unit,
    private val onDeleteClick: (MyFile) -> Unit
) : PagingDataAdapter<MyFile, FileViewHolder>(FileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = StorageItemBinding.inflate(view, parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = getItem(position)
        file?.let {
            holder.bind(
                file,
                onShareClick,
                onDeleteClick = {
                    onDeleteClick(it)
                    deleteItem(position)
                }
            )
            holder.itemView.setOnClickListener {
                onFileClick(file)
            }
        }
    }

    fun deleteItem(position: Int = 0, file: MyFile? = null) {
        val items = this.snapshot().items.toMutableList()
        val pos = if (file == null) position else items.indexOf(file)
        items.removeAt(pos)
        this.notifyItemRemoved(pos)
    }

    fun insertItem(position: Int, file: MyFile) {
        this.snapshot().items.toMutableList().add(position, file)
        this.notifyItemInserted(position)
    }
}
