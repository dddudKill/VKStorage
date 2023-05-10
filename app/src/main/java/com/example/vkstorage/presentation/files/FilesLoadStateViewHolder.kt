package com.example.vkstorage.presentation.files

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.vkstorage.databinding.LoadStateItemBinding

class FilesLoadStateViewHolder(private val binding: LoadStateItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        binding.loadStateProgressBar.visibility = if (loadState == LoadState.Loading) View.VISIBLE else View.GONE
    }
}
