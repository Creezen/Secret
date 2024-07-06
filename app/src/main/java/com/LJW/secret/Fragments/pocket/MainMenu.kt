package com.ljw.secret.fragments.pocket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.ljw.secret.activities.pocket.NewRecord
import com.ljw.secret.customView.UserItemView
import com.ljw.secret.databinding.AddPocketRecordLayoutBinding
import com.ljw.secret.databinding.PocketMainBinding
import com.ljw.secret.dialog.CustomDialog
import com.ljw.secret.dialog.SimpleDialog
import com.ljw.secret.fragments.BaseFragment
import com.ljw.secret.util.DataUtil.msg

class MainMenu: BaseFragment() {

    private lateinit var binding: PocketMainBinding
    private lateinit var userBinding: AddPocketRecordLayoutBinding
    private val userList = ArrayList<String>()

    companion object {
        const val INIT_USER_COUNT = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=PocketMainBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            create.setOnClickListener {
                CustomDialog(
                    requireActivity(),
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
                            Intent(activity, NewRecord::class.java).also {
                                it.putExtra("userData", userList)
                                it.putExtra("title", userBinding.titleEdt.text.toString())
                            }
                        )
                    }
                    userBinding.addBtn.setOnClickListener {
                        addView(userBinding.itemLayout.childCount)
                    }
                    show()
                }
            }
        }
    }

    private fun dialogInit() {
        for (i in 0 until  INIT_USER_COUNT) {
            addView(i)
        }
    }

    private fun addView(position: Int){
        if (position > userList.size - 1) userList.add("")
        userBinding.itemLayout.addView(UserItemView(requireActivity()).also {
            it.edit.hint = "请输入第${position+1}个Player"
            it.edit.setText(userList[position])
            it.setPositon(position)
            it.setOnButtonClick { _,pos ->
                userBinding.itemLayout.removeViewAt(pos)
                userList.removeAt(pos)
                refreshItem(pos)
            }
            it.setOnTextChange { pos, value ->
                userList[pos] = value
            }
        })
    }

    private fun refreshItem(startIndex: Int){
        userBinding.itemLayout.children.forEachIndexed { index, view ->
            if (index < startIndex) return@forEachIndexed
            val itemView = view as UserItemView
            itemView.edit.hint = "请输入第${index+1}个Player"
            itemView.setPositon(index)
            userList[index] = itemView.edit.msg()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userList.clear()
    }
}