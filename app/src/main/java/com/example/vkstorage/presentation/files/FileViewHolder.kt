package com.example.vkstorage.presentation.files

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.vkstorage.common.util.getFileIcon
import com.example.vkstorage.common.util.toReadableFileSize
import com.example.vkstorage.databinding.StorageItemBinding
import com.example.vkstorage.domain.model.MyFile
import java.text.SimpleDateFormat
import java.util.*

class FileViewHolder(private val itemBinding: StorageItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(
        file: MyFile?,
        onShareClick: (MyFile) -> Unit,
        onDeleteClick: (MyFile) -> Unit
    ) {
        file?.let {
            if (!file.isDirectory) {
                itemBinding.fileShareImageButton.visibility = View.VISIBLE
                itemBinding.fileDeleteImageButton.visibility = View.VISIBLE
                itemBinding.fileShareImageButton.setOnClickListener {
                    onShareClick(file)
                }
                itemBinding.fileDeleteImageButton.setOnClickListener {
                    onDeleteClick(file)
                }
            } else {
                itemBinding.fileShareImageButton.visibility = View.GONE
                itemBinding.fileDeleteImageButton.visibility = View.GONE
            }
            itemBinding.fileNameTextView.text = file.name
            val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale("ru", "RU"))
            itemBinding.fileTimestampTextView.text = dateFormat.format(file.lastModified())
            itemBinding.fileSizeTextView.text = file.totalSize.toReadableFileSize()
            itemBinding.fileIconImageView.setImageResource(getFileIcon(file))
        }
    }
}
