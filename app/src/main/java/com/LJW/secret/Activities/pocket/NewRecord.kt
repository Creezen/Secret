package com.ljw.secret.activities.pocket

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ljw.secret.adapter.AddScoreAdapter
import com.ljw.secret.adapter.RecordAdapter
import com.ljw.secret.bean.RecordItem
import com.ljw.secret.databinding.AddRecordDialogBinding
import com.ljw.secret.databinding.AddRecordUserBinding
import com.ljw.secret.databinding.NewPocketRecordBinding
import com.ljw.secret.dialog.CustomDialog
import com.ljw.secret.util.AndroidUtil.addSimpleView
import com.ljw.secret.util.DataUtil.msg
import com.ljw.util.TUtil.toast

class NewRecord : AppCompatActivity() {

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 180
    }

    private lateinit var binding: NewPocketRecordBinding
    private val userList = ArrayList<String>()
    private val scoreList = ArrayList<RecordItem>()
    private val adapter by lazy{
        RecordAdapter(scoreList)
    }
    private val addScoreAdapter by lazy {
        AddScoreAdapter(userList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewPocketRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        userList.clear()
        intent.getStringArrayListExtra("userData")?.let { userNames ->
            userList.addAll(
                userNames.filter { it.isNotEmpty() }.toSet()
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        with(binding) {
            titleTv.text = intent.getStringExtra("title")
            userList.forEach{ name ->
                playerName.addSimpleView(name, WIDTH)
            }
            addUser.setOnClickListener {
                CustomDialog(
                    this@NewRecord,
                    AddRecordUserBinding.inflate(layoutInflater)
                ).apply{
                    setTitle("添加角色")
                    setCustomLeftButton{ binding, _ ->
                        dismiss()
                    }
                    setCustomRightButton("添加") { bind, _ ->
                        val addUserName = bind.edit.msg()
                        playerName.addSimpleView(addUserName, WIDTH)
                        userList.add(addUserName)
                        scoreList.forEach {
                            it.scores.add(0)
                            Log.e("NewRecord.initView","${it.scores.size}")
                        }
                        adapter.notifyDataSetChanged()
                        addScoreAdapter.notifyUserAdded()
                        dismiss()
                    }
                    show()
                }
            }
            addRecord.setOnClickListener {
                CustomDialog(
                    this@NewRecord,
                    AddRecordDialogBinding.inflate(layoutInflater)
                ).apply {
                    val rv = viewBinding.scoreRv
                    rv.layoutManager = LinearLayoutManager(this@NewRecord)
                    rv.adapter = addScoreAdapter
                    setTitle("设置分数")
                    setCustomLeftButton { _, _ ->
                        dismiss()
                    }
                    setCustomRightButton("添加") { _, _ ->
                        val scoreList = addScoreAdapter.getscoreList()
                        val newList = ArrayList<Int>()
                        newList.addAll(scoreList)
                        if (newList.sum() != 0) {
                            "分数设置不合法，请检查一下哦".toast()
                            return@setCustomRightButton
                        }
                        addRecord(newList)
                        dismiss()
                    }
                    show()
                }
            }
            rv.layoutManager = LinearLayoutManager(this@NewRecord)
            rv.adapter = adapter
            rvSC.setOnScrollChangeListener { _, i, i2, _, _ ->
                playerNameSV.scrollTo(i, i2)
                totalScoreSV.scrollTo(i, i2)
            }
            rv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val scX = roundNumRV.scrollX
                val scY = roundNumRV.scrollY
                roundNumRV.scrollTo(scX-oldScrollX, scY-oldScrollY)
            }
            playerNameSV.setOnTouchListener { _, _ -> true }
            totalScoreSV.setOnTouchListener { _, _ -> true }
            roundNumRV.setOnTouchListener { _, _ -> true }
        }
    }

    private fun addRecord(list: ArrayList<Int>) {
        scoreList.add(RecordItem("${System.currentTimeMillis()}", list))
        adapter.notifyItemInserted(scoreList.size)
        binding.roundNum.addSimpleView("${scoreList.size}", HEIGHT, WIDTH)
    }
}