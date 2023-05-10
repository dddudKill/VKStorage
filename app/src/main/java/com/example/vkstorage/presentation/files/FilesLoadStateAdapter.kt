package com.example.vkstorage.presentation.files

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.vkstorage.databinding.LoadStateItemBinding

class FilesLoadStateAdapter : LoadStateAdapter<FilesLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: FilesLoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FilesLoadStateViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = LoadStateItemBinding.inflate(view, parent, false)
        return FilesLoadStateViewHolder(binding)
    }
}
