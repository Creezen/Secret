package com.jayce.vexis.business.kit.book

import android.content.Intent
import android.os.Bundle
import com.creezen.commontool.Config.NIL
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.business.kit.book.archive.ArchiveActivity
import com.jayce.vexis.business.kit.book.note.LineNoteActivity
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityBookBinding
import com.jayce.vexis.databinding.BookAddDialogBinding
import com.jayce.vexis.foundation.ui.block.FlexibleDialog

class BookActivity : BaseActivity<ActivityBookBinding>() {

    private val userList = ArrayList<String>()

    companion object {
        const val INIT_USER_COUNT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {
        create.setOnClickListener {
            FlexibleDialog
                .flexibleView<BookAddDialogBinding>(this@BookActivity) {
                    repeat(INIT_USER_COUNT) { i -> addView(this, i) }
                    addBtn.setOnClickListener {
                        addView(this, itemLayout.childCount)
                    }
                }
                .positive {
                    val intent = Intent(this@BookActivity, LineNoteActivity::class.java)
                    intent.apply {
                        putExtra("userData", userList)
                        putExtra("title", titleEdt.msg())
                        putExtra("time", System.currentTimeMillis())
                    }
                    startActivity(intent)
                }
                .show()
        }
        history.setOnClickListener {
            startActivity(Intent(this@BookActivity, ArchiveActivity::class.java))
        }
    }

    private fun addView(bind: BookAddDialogBinding, position: Int) {
        userList.add(NIL)
        bind.itemLayout.addView(
            BookDialogView(this, null,).also {
                it.binding.nameEdt.hint = "请输入第${position + 1}个Player"
                it.binding.nameEdt.setText(userList[position])
                it.setPositon(position)
                it.setOnButtonClick { _, pos ->
                    bind.itemLayout.removeViewAt(pos)
                    userList.removeAt(pos)
                    refreshItem(bind, pos)
                }
                it.setTextChange { pos, value ->
                    userList[pos] = value
                }
            }
        )
    }

    private fun refreshItem(bind: BookAddDialogBinding, startIndex: Int) {
        with(bind.itemLayout) {
            for (index in 0 until childCount) {
                if (index < startIndex) continue
                val itemView = getChildAt(index) as BookDialogView
                itemView.binding.nameEdt.hint = "请输入第${index + 1}个Player"
                itemView.setPositon(index)
                userList[index] = itemView.binding.nameEdt.msg()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userList.clear()
    }
}