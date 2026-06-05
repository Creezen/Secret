package com.jayce.vexis.foundation.ui.block.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.jayce.vexis.databinding.ImageDialogBinding

class ImageDialog(private val mContext: Context) : DialogFragment(){

    private var imageId: Int? = null
    private var imageUri: Uri? = null
    private var imageDrawable: Drawable? = null
    private lateinit var binding: ImageDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ImageDialogBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView()= binding.apply {
        image.setDraggable(true)
        image.setFullScreen()
        when {
            imageId != null -> image.setImageResource(imageId ?: 0)
            imageDrawable != null -> image.setImageDrawable(imageDrawable)
            imageUri != null -> image.setImageURI(imageUri)
        }
    }

    fun show(id: Int) {
        imageId = id
        show()
    }

    fun show(drawable: Drawable) {
        imageDrawable = drawable
        show()
    }

    fun show(uri: Uri) {
        imageUri = uri
        show()
    }

    private fun show() {
        val manager = (mContext as? FragmentActivity)?.supportFragmentManager ?: return
        super.show(manager, "ScaleImage")
    }
}