package com.jayce.vexis.foundation.view.block

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.jayce.vexis.R
import com.jayce.vexis.databinding.DialogBinding

class FlexibleDialog<T: ViewBinding>(
    private val mContext: Context,
    val inflater: LayoutInflater,
    themeId: Int = R.style.Dialog,

){
    companion object {
        private const val TAG = "FlexibleDialog"
    }

    private var positiveFlag: Int = -1
    private var negativeFlag: Int = -1
    private var neutralFlag: Int = -1
    private var positiveCallback: ((Int, FlexibleDialog<T>) -> Unit)? = null
    private var negativeCallback: ((Int, FlexibleDialog<T>) -> Unit)? = null
    private var neutralCallback: ((Int, FlexibleDialog<T>) -> Unit)? = null
    private var dialog: AlertDialog? = null
    private var mBinding: T? = null
    private val builder: AlertDialog.Builder = AlertDialog.Builder(mContext, themeId)

    private val defaultSize by lazy {
        val metrics = mContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        FlexibleSize(width - 100, height * 3/4)
    }

    init {
        mBinding = DialogBinding.inflate(inflater) as? T
        builder.setView(mBinding?.root)
    }

    fun <K: ViewBinding> flexibleView(binding: K, func: (K.() -> Unit)? = null): FlexibleDialog<T> {
        if (mBinding is DialogBinding) {
            val bind = mBinding as? DialogBinding
            bind?.apply {
                replaceLayout.removeAllViews()
                replaceLayout.addView(binding.root)
            }
            if (func != null) {
                binding.func()
            }
        }
        return this
    }

    fun flexibleView(
        factory: ((LayoutInflater) -> T)? = null,
        func: (T.(FlexibleDialog<T>) -> Unit)? = null
    ): FlexibleDialog<T> {
        if (factory != null) {
            mBinding = factory(inflater)
            builder.setView(mBinding?.root)
        }
        if (func != null) {
            mBinding?.func(this)
        }
        return this
    }

    fun title(resId: Int): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return this.title(text)
    }

    fun title(title: String): FlexibleDialog<T> {
        if (mBinding is DialogBinding) {
            val bind = mBinding as DialogBinding
            bind.title.text = title
        } else {
            builder.setTitle(title)
        }
        return this
    }

    fun title(view: View): FlexibleDialog<T> {
        builder.setCustomTitle(view)
        return this
    }

    fun cancelable(isCancle: Boolean): FlexibleDialog<T> {
        builder.setCancelable(isCancle)
        return this
    }

    fun positive(resId: Int, autoDismiss: Boolean = true, onclick: T.() -> Int): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return positive(text, autoDismiss, onclick)
    }

    fun negative(resId: Int, autoDismiss: Boolean = true, onclick: T.() -> Int): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return negative(text, autoDismiss, onclick)
    }

    fun neutral(resId: Int, autoDismiss: Boolean = true, onclick: T.() -> Int): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return neutral(text, autoDismiss, onclick)
    }

    fun positive(text: String = "确定", autoDismiss: Boolean = true, onclick: T.() -> Int): FlexibleDialog<T> {
        if (mBinding is DialogBinding) {
            val bind = mBinding as DialogBinding
            bind.yes.visibility = View.VISIBLE
            bind.yes.text = text
            bind.yes.setOnClickListener {
                mBinding?.let {
                    positiveFlag = onclick.invoke(it)
                }
                if (positiveFlag > 0) {
                    positiveCallback?.invoke(positiveFlag, this)
                }
                if (autoDismiss) {
                    dismiss()
                }
            }
        } else {
            builder.setPositiveButton(text) { _, _ ->
                mBinding?.let {
                    positiveFlag = onclick.invoke(it)
                }
                if (positiveFlag > 0) {
                    positiveCallback?.invoke(positiveFlag, this)
                }
                if (autoDismiss) {
                    dismiss()
                }
            }
        }
        return this
    }

    fun negative(text: String = "取消", autoDismiss: Boolean = true, onclick: T.() -> Int): FlexibleDialog<T> {
        if (mBinding is DialogBinding) {
            val bind = mBinding as DialogBinding
            bind.no.visibility = View.VISIBLE
            bind.no.text = text
            bind.no.setOnClickListener {
                mBinding?.let {
                    negativeFlag = onclick.invoke(it)
                }
                if (negativeFlag > 0) {
                    negativeCallback?.invoke(negativeFlag, this)
                }
                if (autoDismiss) {
                    dismiss()
                }
            }
        } else {
            builder.setNegativeButton(text) { dialog, _ ->
                mBinding?.let {
                    negativeFlag = onclick.invoke(it)
                }
                if (negativeFlag > 0) {
                    negativeCallback?.invoke(negativeFlag, this)
                }
                if (autoDismiss) {
                    dismiss()
                }
            }
        }

        return this
    }

    fun neutral(text: String = "取消", autoDismiss: Boolean = true, onclick: T.() -> Int): FlexibleDialog<T> {
        builder.setNeutralButton(text) { dia, _ ->
            mBinding?.let {
                neutralFlag = onclick.invoke(it)
            }
            if (neutralFlag > 0) {
                neutralCallback?.invoke(neutralFlag, this)
            }
            if (autoDismiss) {
                dismiss()
            }
        }
        return this
    }

    fun positiveHandle(func: (Int, FlexibleDialog<T>) -> Unit): FlexibleDialog<T> {
        positiveCallback = func
        return this
    }

    fun negativeHandle(func: (Int, FlexibleDialog<T>) -> Unit): FlexibleDialog<T> {
        negativeCallback = func
        return this
    }

    fun neutralHandle(func: (Int, FlexibleDialog<T>) -> Unit): FlexibleDialog<T> {
        neutralCallback = func
        return this
    }

    fun show(flexibleSize: FlexibleSize? = null): FlexibleDialog<T> {
        if (dialog == null) {
            dialog = builder.create()
        }
        dialog?.show()
        dialog?.window?.apply {
            if (flexibleSize != null) {
                setLayout(flexibleSize.width, LayoutParams.WRAP_CONTENT)
            } else {
                setLayout(defaultSize.width, LayoutParams.WRAP_CONTENT)
            }
        }
        return this
    }

    fun onDismiss(onDismiss: FlexibleDialog<T>.() -> Unit): FlexibleDialog<T> {
        builder.setOnDismissListener {
            onDismiss.invoke(this)
        }
        return this
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    data class FlexibleSize(val width: Int, val height: Int)
}