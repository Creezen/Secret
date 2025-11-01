package com.jayce.vexis.business.kit.ledger

import android.content.Intent
import android.os.Bundle
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.AddPocketRecordLayoutBinding
import com.jayce.vexis.databinding.DialogBinding
import com.jayce.vexis.databinding.PocketMainBinding
import com.jayce.vexis.foundation.view.block.FlexibleDialog
import java.util.ArrayList

class LedgerSheetActivity : BaseActivity<PocketMainBinding>() {

    private lateinit var userBinding: AddPocketRecordLayoutBinding
    private val userList = ArrayList<String>()

    companion object {
        const val TAG = "LedgerSheetActivity"
        const val INIT_USER_COUNT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            create.setOnClickListener {
                FlexibleDialog<DialogBinding>(this@LedgerSheetActivity, layoutInflater)
                    .flexibleView(AddPocketRecordLayoutBinding.inflate(layoutInflater)) {
                        userBinding = this
                        dialogInit()
                        addBtn.setOnClickListener {
                            addView(userBinding.itemLayout.childCount)
                        }
                    }
                    .positive {
                        startActivity(
                            Intent(this@LedgerSheetActivity, ScoreBoardActivity::class.java).also {
                                it.putExtra("userData", userList)
                                it.putExtra("title", userBinding.titleEdt.text.toString())
                                it.putExtra("time", System.currentTimeMillis())
                            },
                        )
                        return@positive -1
                    }
                    .show()
            }
            history.setOnClickListener {
                startActivity(Intent(this@LedgerSheetActivity, LedgerHistoryActivity::class.java))
            }
        }
    }

    private fun dialogInit() {
        for (i in 0 until INIT_USER_COUNT) {
            addView(i)
        }
    }

    private fun addView(position: Int) {
        userList.add(EMPTY_STRING)
        userBinding.itemLayout.addView(
            ScoreInsertEntryView(
                this,
                null,
            ).also {
                it.binding.nameEdt.hint = "请输入第${position + 1}个Player"
                it.binding.nameEdt.setText(userList[position])
                it.setPositon(position)
                it.setOnButtonClick { _, pos ->
                        userBinding.itemLayout.removeViewAt(pos)
                        userList.removeAt(pos)
                        refreshItem(pos)
                    }
                it.setTextChange { pos, value ->
                    userList[pos] = value
                }
        })
    }

    private fun refreshItem(startIndex: Int) {
        with(userBinding.itemLayout) {
            for (index in 0 until childCount) {
                if (index < startIndex) {
                    continue
                }
                val itemView = getChildAt(index) as ScoreInsertEntryView
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