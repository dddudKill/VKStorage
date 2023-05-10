package com.example.vkstorage.presentation.files

import androidx.recyclerview.widget.DiffUtil
import com.example.vkstorage.domain.model.MyFile

class FileDiffCallback : DiffUtil.ItemCallback<MyFile>() {
    override fun areItemsTheSame(oldItem: MyFile, newItem: MyFile): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MyFile, newItem: MyFile): Boolean {
        return oldItem == newItem
    }
}
