package com.example.vkstorage.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vkstorage.R
import com.example.vkstorage.common.Constants
import com.example.vkstorage.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkPermission()) {
            if (savedInstanceState == null) {
                startFragment()
            }
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.permission_required_snackbar_message),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(R.string.permission_required_snackbar_action)) {
                    storageActivityResultLauncher.launch(intent)
                }.show()
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.permission_required_snackbar_message),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(R.string.permission_required_snackbar_action)) {
                    storageActivityResultLauncher.launch(intent)
                }.show()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.PERMISSION_REQUEST_STORAGE
            )
        }
    }

    private val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                startFragment()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_REQUEST_STORAGE) {
            if (grantResults.isNotEmpty()) {
                val read = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val write = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (read && write) {
                    startFragment()
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, StorageFragment.newInstance())
            .commitNow()

        // val fileHashWorkerManager = FileHashWorkerManager(applicationContext)
        // fileHashWorkerManager.start()
    }
}
