package com.jayce.vexis.foundation.view.block

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jayce.vexis.R

class DownloadButtonSheetDialog(
    private val callback: BottomSheetDialog.() -> Unit
) : BottomSheetDialogFragment() {

    private var dismissCallback: (() -> Unit)?  = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = layoutInflater.inflate(R.layout.download_bottom_sheet, null)
        dialog.setContentView(view)
        callback.invoke(dialog)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.invoke()
    }

    fun onDismiss(callback: () -> Unit) {
        dismissCallback = callback
    }

}