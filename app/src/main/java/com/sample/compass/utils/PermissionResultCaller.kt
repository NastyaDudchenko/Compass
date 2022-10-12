package com.sample.compass.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionResultCaller(
    resultCaller: ActivityResultCaller,
    private val context: Context
) {
    private var onGranted: ((Boolean) -> Unit)? = null

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        getPermissionResult(resultCaller)

    private fun getPermissionResult(resultCaller: ActivityResultCaller): ActivityResultLauncher<Array<String>> =
        resultCaller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            onGranted?.invoke(permissions.all(Map.Entry<String, Boolean>::value))
        }

    fun onStart(
        permissions: Array<String>,
        onGranted: ((Boolean) -> Unit)? = null
    ) {
        this.onGranted = onGranted
        val isGranted = isGranted(permissions)
        if (isGranted) {
            onGranted?.invoke(isGranted)
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    fun isGranted(permissions: Array<String>) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
