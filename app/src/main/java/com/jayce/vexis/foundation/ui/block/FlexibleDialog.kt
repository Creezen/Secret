package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.jayce.vexis.client.DataTool.dpToPx
import com.jayce.vexis.client.WindowTool
import com.jayce.vexis.R
import com.jayce.vexis.databinding.DialogBinding

class FlexibleDialog<T : ViewBinding>(
    private val mContext: Context,
    createChild: (LayoutInflater) -> T,
    func: (T.(FlexibleDialog<T>) -> Unit)? = null,
    themeId: Int = R.style.Dialog
) {

    companion object {
        fun flexibleViewNormal(
            context: Context,
            themeId: Int = R.style.Dialog,
            func: (DialogBinding.(FlexibleDialog<DialogBinding>) -> Unit)? = null
        ): FlexibleDialog<DialogBinding> {
            return flexibleView<DialogBinding>(context, themeId, func)
        }

        inline fun <reified U: ViewBinding> flexibleView(
            context: Context,
            themeId: Int = R.style.Dialog,
            noinline func: (U.(FlexibleDialog<U>) -> Unit)? = null
        ): FlexibleDialog<U> {
            val createChild = { inflater: LayoutInflater ->
                val method = U::class.java.getMethod("inflate", LayoutInflater::class.java)
                method.invoke(null, inflater) as U
            }
            return FlexibleDialog(context, createChild, func, themeId)
        }
    }

    private val inflater = LayoutInflater.from(mContext)
    private val rootBinding = DialogBinding.inflate(inflater)
    private val binding: T = createChild(inflater)
    private var dialog: AlertDialog? = null
    private val builder: AlertDialog.Builder

    private val defaultSize by lazy {
        val metrics = WindowTool.screenMetrics(mContext)
        FlexibleSize(metrics.first - 100, metrics.second * 3 / 4)
    }

    init {
        val realBind = if (binding is DialogBinding) rootBinding else binding
        if (binding !is DialogBinding) {
            rootBinding.replaceLayout.removeAllViews()
            rootBinding.replaceLayout.addView(binding.root)
        }
        builder = AlertDialog.Builder(mContext, themeId).apply { setView(rootBinding.root) }
        if (func != null) {
            (realBind as? T)?.func(this)
        }
    }

    fun title(resId: Int): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return title(text)
    }

    fun title(title: String): FlexibleDialog<T> {
        rootBinding.titleContainer.visibility = View.VISIBLE
        rootBinding.title.visibility = View.VISIBLE
        rootBinding.title.text = title
        return this
    }

    fun title(view: View): FlexibleDialog<T> {
        rootBinding.titleContainer.visibility = View.VISIBLE
        rootBinding.titleContainer.removeAllViews()
        rootBinding.titleContainer.addView(view)
        return this
    }

    fun cancelable(isCancle: Boolean): FlexibleDialog<T> {
        builder.setCancelable(isCancle)
        return this
    }

    fun positive(resId: Int, autoDismiss: Boolean = true, onclick: (T.(FlexibleDialog<T>) -> Unit)? = null): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return positive(text, autoDismiss, onclick)
    }

    fun negative(resId: Int, autoDismiss: Boolean = true, onclick: (T.(FlexibleDialog<T>) -> Unit)? = null): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return negative(text, autoDismiss, onclick)
    }

    fun neutral(resId: Int, autoDismiss: Boolean = true, onclick: (T.(FlexibleDialog<T>) -> Unit)? = null): FlexibleDialog<T> {
        val text = mContext.getString(resId)
        return neutral(text, autoDismiss, onclick)
    }

    fun positive(label: String = "确定", autoDismiss: Boolean = true, onclick: (T.(FlexibleDialog<T>) -> Unit)? = null): FlexibleDialog<T> {
        rootBinding.positiveBtn.apply {
            visibility = View.VISIBLE
            text = label
            setOnClickListener {
                onclick?.invoke(binding, this@FlexibleDialog)
                if (autoDismiss) dismiss()
            }
        }

        return this
    }

    fun negative(label: String = "取消", autoDismiss: Boolean = true, onclick: (T.(FlexibleDialog<T>) -> Unit)? = null): FlexibleDialog<T> {
        rootBinding.negativeBtn.apply {
            visibility = View.VISIBLE
            text = label
            setOnClickListener {
                onclick?.invoke(binding, this@FlexibleDialog)
                if (autoDismiss) dismiss()
            }
        }
        return this
    }

    fun neutral(label: String = "取消", autoDismiss: Boolean = true, onclick: (T.(FlexibleDialog<T>) -> Unit)? = null): FlexibleDialog<T> {
        rootBinding.neuralBtn.apply {
            visibility = View.VISIBLE
            text = label
            setOnClickListener {
                onclick?.invoke(binding, this@FlexibleDialog)
                if (autoDismiss) dismiss()
            }
        }
        return this
    }

    fun show(flexibleSize: FlexibleSize? = null): FlexibleDialog<T> {
        if (dialog == null) {
            dialog = builder.create()
        }
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.show()
        dialog?.window?.apply {
            // 清除不准获取焦点的flag，（否则输入框会获取不到焦点，导致不弹出输入法）
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            setBackgroundDrawableResource(R.drawable.corner_24)

            if (flexibleSize == null) {
                setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                return this@FlexibleDialog
            }
            if (flexibleSize.width == -1 && flexibleSize.height == -1) {
                setLayout(defaultSize.width, defaultSize.height)
            } else {
                setLayout(flexibleSize.width.toFloat().dpToPx().toInt(), LayoutParams.WRAP_CONTENT)
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