package com.example.vkstorage.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkstorage.R
import com.example.vkstorage.common.Constants
import com.example.vkstorage.common.util.FileOrder
import com.example.vkstorage.common.util.OrderType
import com.example.vkstorage.databinding.FragmentStorageBinding
import com.example.vkstorage.domain.model.MyFile
import com.example.vkstorage.presentation.files.FileAdapter
import com.example.vkstorage.presentation.files.FilesLoadStateAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

@AndroidEntryPoint
class StorageFragment : Fragment() {

    companion object {
        fun newInstance() = StorageFragment()
    }

    private val storageViewModel by viewModels<StorageViewModel>()
    private val fileAdapter = FileAdapter(
        onFileClick = {
            onFileClick(it)
        },
        onShareClick = {
            onShareClick(it)
        },
        onDeleteClick = {
            onDeleteClick(it)
        }
    )
    private val loadStateAdapter = FilesLoadStateAdapter()
    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!

    private var currentPath: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                var count = 0
                override fun handleOnBackPressed() {
                    if (currentPath?.parentFile != null && currentPath?.absolutePath != Constants.BASE_PATH || currentPath == null) {
                        storageViewModel.onEvent(StorageEvents.GoBack)
                        count = 0
                    } else {
                        if (count++ == 0) {
                            Toast.makeText(context, getString(R.string.on_back_pressed_toast), Toast.LENGTH_SHORT).show()
                        } else {
                            count = 0
                            requireActivity().finish()
                            exitProcess(0)
                        }
                    }
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = storageViewModel.state
        initViews(state)
        viewLifecycleOwner.lifecycleScope.launch {
            state.collectLatest { state ->
                currentPath = state.currentPath
                if (state.isOrderSectionVisible) {
                    binding.filterLayoutLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.filterLayoutLinearLayout.visibility = View.GONE
                }
                if (state.isDeletingFile == binding.filterNameRadioButton.isEnabled) {
                    setFiltersEnabled(!state.isDeletingFile)
                }
                if (state.isShowingRecent) {
                    binding.recentImageButton.visibility = View.GONE
                    binding.currentPathTextView.text = getString(R.string.recent_changes_as_path_name)
                    if (state.isLoadingRecent) {
                        binding.storageProgressBar.visibility = View.VISIBLE
                    } else {
                        binding.storageProgressBar.visibility = View.GONE
                        if (state.error.isBlank()) {
                            if (state.isAbleToShowRecent) {
                                storageViewModel.onEvent(StorageEvents.ShowRecentlyChanged)
                            }
                        } else {
                            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.storageProgressBar.visibility = View.GONE
                    binding.recentImageButton.visibility = View.VISIBLE
                    binding.currentPathTextView.text = state.currentPath?.absolutePath
                }
                fileAdapter.submitData(state.files)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        storageViewModel.onEvent(StorageEvents.DeleteFile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews(state: StateFlow<StorageStates>) {
        val recyclerView = binding.storageListRecyclerView
        recyclerView.apply {
            adapter = fileAdapter.apply {
                stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            }.withLoadStateFooter(footer = loadStateAdapter)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        fileAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.storageProgressBar.visibility = View.VISIBLE
            } else {
                binding.storageProgressBar.visibility = View.GONE
            }
        }
        binding.filterImageButton.setOnClickListener {
            storageViewModel.onEvent(StorageEvents.ToggleOrderSection)
        }
        binding.filterNameRadioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                storageViewModel.onEvent(StorageEvents.Order(FileOrder.Title(state.value.fileOrder.orderType)))
            }
        }
        binding.filterSizeRadioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                storageViewModel.onEvent(StorageEvents.Order(FileOrder.Size(state.value.fileOrder.orderType)))
            }
        }
        binding.filterTimestampRadioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                storageViewModel.onEvent(StorageEvents.Order(FileOrder.Date(state.value.fileOrder.orderType)))
            }
        }
        binding.filterExtensionRadioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                storageViewModel.onEvent(StorageEvents.Order(FileOrder.Extension(state.value.fileOrder.orderType)))
            }
        }
        binding.filterOrderAscendingRadioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                storageViewModel.onEvent(StorageEvents.Order(state.value.fileOrder.copy(OrderType.Ascending)))
            }
        }
        binding.filterOrderDescendingRadioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                storageViewModel.onEvent(StorageEvents.Order(state.value.fileOrder.copy(OrderType.Descending)))
            }
        }

        binding.recentImageButton.setOnClickListener {
            if (!state.value.isShowingRecent) {
                storageViewModel.onEvent(StorageEvents.ToggleRecentlyChanged)
            }
        }
    }

    private fun setFiltersEnabled(isEnabled: Boolean) {
        for (i in 0 until binding.filterTypeRadioGroup.childCount) {
            binding.filterTypeRadioGroup.getChildAt(i).isEnabled = isEnabled
        }
        for (i in 0 until binding.filterOrderRadioGroup.childCount) {
            binding.filterOrderRadioGroup.getChildAt(i).isEnabled = isEnabled
        }
    }

    private fun onDeleteClick(file: MyFile) {
        storageViewModel.onEvent(StorageEvents.DeletingFileStarted(file))
        val snackbar = Snackbar.make(binding.storageListRecyclerView, getString(R.string.file_deleted_snackbar_message), Snackbar.LENGTH_LONG)
        snackbar.setAction(getString(R.string.undo_deletion_snackbar_message)) {
            if (currentPath?.listFiles()?.contains(file) == true) {
                val position = fileAdapter.snapshot().items.indexOf(file)
                fileAdapter.insertItem(position, file)
                if (position == 0) {
                    binding.storageListRecyclerView.scrollToPosition(0)
                }
            }
            storageViewModel.onEvent(StorageEvents.UndoDeletingFile)
        }
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event != DISMISS_EVENT_ACTION) {
                    storageViewModel.onEvent(StorageEvents.DeleteFile)
                }
            }
        })
        snackbar.show()
    }

    private fun onFileClick(file: MyFile) {
        if (file.isDirectory) {
            storageViewModel.onEvent(StorageEvents.OpenDirectory(file))
            return
        }

        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.name)
        var type = mimeTypeMap.getMimeTypeFromExtension(extension)

        if (type == null) {
            type = "*/*"
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val path = FileProvider.getUriForFile(
                requireActivity(),
                getString(R.string.authority),
                file
            )
            intent.setDataAndType(path, type)
            val chooser = Intent.createChooser(intent, null)
            startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.unable_to_open_file_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onShareClick(file: MyFile) {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.name)
        var type = mimeTypeMap.getExtensionFromMimeType(extension)

        if (type == null) {
            type = "*/*"
        }

        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_intent_message))
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val path = FileProvider.getUriForFile(
                requireActivity(),
                getString(R.string.authority),
                file
            )
            intent.putExtra(Intent.EXTRA_STREAM, path)
            intent.type = "*/*"
            val chooser = Intent.createChooser(intent, null)
            startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, getString(R.string.no_apps_found_exception), Toast.LENGTH_SHORT).show()
        }
    }
}
