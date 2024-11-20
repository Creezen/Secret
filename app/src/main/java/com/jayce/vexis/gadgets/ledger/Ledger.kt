package com.jayce.vexis.gadgets.ledger

import android.content.Intent
import android.os.Bundle
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.AddPocketRecordLayoutBinding
import com.jayce.vexis.databinding.PocketMainBinding
import com.jayce.vexis.widgets.CustomDialog
import com.jayce.vexis.widgets.SimpleDialog

class Ledger: BaseActivity() {

    private lateinit var binding: PocketMainBinding
    private lateinit var userBinding: AddPocketRecordLayoutBinding
    private val userList = ArrayList<String>()

    companion object {
        const val TAG = "Ledger"
        const val INIT_USER_COUNT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PocketMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding) {
            create.setOnClickListener {
                CustomDialog(
                    this@Ledger,
                    AddPocketRecordLayoutBinding.inflate(layoutInflater)
                ).apply {
                    setVisible(SimpleDialog.TITLE_INVISIBLE)
                    userBinding = this.viewBinding
                    dialogInit()
                    setCustomLeftButton("取消") { _,dialog ->
                        dialog.dismiss()
                    }
                    setCustomRightButton("确定") { _, dialog ->
                        dialog.dismiss()
                        startActivity(
                            Intent(this@Ledger, ScoreBoard::class.java).also {
                                it.putExtra("userData", userList)
                                it.putExtra("title", userBinding.titleEdt.text.toString())
                                it.putExtra("time", System.currentTimeMillis())
                            }
                        )
                    }
                    userBinding.addBtn.setOnClickListener {
                        addView(userBinding.itemLayout.childCount)
                    }
                    show()
                }
            }
            history.setOnClickListener {
                startActivity(Intent(this@Ledger, RecordHistoryActivity::class.java))

            }
        }
    }

    private fun dialogInit() {
        for (i in 0 until  INIT_USER_COUNT) {
            addView(i)
        }
    }

    private fun addView(position: Int) {
        userList.add("")
        userBinding.itemLayout.addView(ScoreInsertEntryView(this, null).also {
            it.binding.nameEdt.hint = "请输入第${position+1}个Player"
            it.binding.nameEdt.setText(userList[position])
            it.setPositon(position)
            it.setOnButtonClick { _,pos ->
                userBinding.itemLayout.removeViewAt(pos)
                userList.removeAt(pos)
                refreshItem(pos)
            }
            it.setTextChange { pos, value ->
                userList[pos] = value
            }
        })
    }

    private fun refreshItem(startIndex: Int){
        with(userBinding.itemLayout) {
            for (index in 0 until childCount) {
                if (index < startIndex) {
                    continue
                }
                val itemView = getChildAt(index) as ScoreInsertEntryView
                itemView.binding.nameEdt.hint = "请输入第${index+1}个Player"
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