package com.jayce.vexis.core.base

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

interface Common<K : ViewBinding> : ActivityResultCaller {

    fun <K: ViewBinding> getBind() : K {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<K>
        val method = type.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, getLayoutInflate()) as K
    }

    fun getLayoutInflate(): LayoutInflater

    fun openFile(): ActivityResultContract<Array<String>, Uri?> {
        return ActivityResultContracts.OpenDocument()
    }

    fun getPermission(): ActivityResultContract<Array<String>, Map<String, Boolean>> {
        return ActivityResultContracts.RequestMultiplePermissions()
    }

    fun startActivity(): ActivityResultContract<Intent, ActivityResult> {
        return ActivityResultContracts.StartActivityForResult()
    }

    fun <I, O> getLauncher(
        contract: ActivityResultContract<I, O>,
        action: (O) -> Unit,
    ): ActivityResultLauncher<I> {
        return registerForActivityResult(contract) {
            action(it)
        }
    }
}