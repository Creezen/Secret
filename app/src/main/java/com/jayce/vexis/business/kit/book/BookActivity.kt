package com.jayce.vexis.business.kit.book

import android.os.Bundle
import androidx.core.view.children
import com.jayce.vexis.business.kit.book.archive.ArchiveActivity
import com.jayce.vexis.business.kit.book.note.LineNoteActivity
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityBookBinding
import com.jayce.vexis.databinding.BookAddDialogBinding
import com.jayce.vexis.foundation.Util.Extension.jumpTo
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import com.jayce.vexis.util.Config.NIL

class BookActivity : BaseActivity<ActivityBookBinding>() {

    private val playerList = ArrayList<String>()

    companion object {
        private const val INIT_COUNT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {
        create.setOnClickListener {
            FlexibleDialog.flexibleView<BookAddDialogBinding>(this@BookActivity) {
                repeat(INIT_COUNT) { i -> addView(this, i) }
            }
            .positive(autoDismiss = false) {
                if (startRecord(this)) it.dismiss()
            }
            .negative("添加", false) {
                addView(this, itemLayout.childCount)
                setDelete(this)
            }
            .show()
        }
        history.setOnClickListener { jumpTo(ArchiveActivity::class.java) }
    }

    private fun addView(bind: BookAddDialogBinding, position: Int) {
        playerList.add(NIL)
        val view = BookDialogView(this, null)
        view.refresh("Player ${position + 1}", playerList[position], position)
        view.setOnItemDelete { _, pos ->
            bind.itemLayout.removeViewAt(pos)
            playerList.removeAt(pos)
            refreshItem(bind, pos)
        }
        view.setTextChange { pos, value -> playerList[pos] = value }
        bind.itemLayout.addView(view)
    }

    private fun refreshItem(bind: BookAddDialogBinding, startIndex: Int) {
        val layout = bind.itemLayout
        setDelete(bind)
        for (index in startIndex until layout.childCount) {
            val itemView = layout.getChildAt(index) as BookDialogView
            itemView.refresh("Player ${index + 1}", "", index)
            playerList[index] = itemView.content()
        }
    }

    private fun setDelete(bind: BookAddDialogBinding) {
        val layout = bind.itemLayout
        val count = layout.childCount
        val shouldDelete = count > INIT_COUNT
        for (i in 0 until count) {
            val itemView = layout.getChildAt(i) as BookDialogView
            itemView.enableDelete(shouldDelete)
        }
    }

    private fun startRecord(bind: BookAddDialogBinding): Boolean {
        bind.apply {
            if (title.msg().isEmpty()) {
                "标题不可以为空!".toast()
                return false
            }
            itemLayout.children.forEach {
                val child = it as BookDialogView
                if (child.content().isNotEmpty()) return@forEach
                "有子项为空，请检查!".toast()
                return false
            }
            jumpTo(LineNoteActivity::class.java) {
                putExtra("players", playerList)
                putExtra("title", title.msg())
                putExtra("time", System.currentTimeMillis())
            }
            return true
        }
    }
}