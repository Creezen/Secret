package com.jayce.vexis.base

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

interface Common: ActivityResultCaller {

    fun openFile(): ActivityResultContract<Array<String>, Uri?> {
        return ActivityResultContracts.OpenDocument()
    }

    fun getPermission(): ActivityResultContract<String, Boolean> {
        return ActivityResultContracts.RequestPermission()
    }

    fun startActivity(): ActivityResultContract<Intent, ActivityResult> {
        return ActivityResultContracts.StartActivityForResult()
    }

    fun <I, O> getLauncher(
        contract: ActivityResultContract<I, O>,
        action: (O) -> Unit
    ): ActivityResultLauncher<I> {
        return registerForActivityResult(contract) {
            action(it)
        }
    }
}