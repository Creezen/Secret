package com.jayce.vexis.utility.ledger

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.AndroidTool.addSimpleView
import com.jayce.vexis.databinding.AddRecordDialogBinding
import com.jayce.vexis.databinding.AddRecordUserBinding
import com.jayce.vexis.databinding.NewPocketRecordBinding
import com.jayce.vexis.stylized.CustomDialog
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.DataTool.toTime

class NewRecord : AppCompatActivity() {

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 180
    }

    private lateinit var binding: NewPocketRecordBinding
    private val userList = ArrayList<String>()
    private val scoreList = ArrayList<RecordItem>()
    private val totalScoreList = ArrayList<Int>()
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
        repeat(userList.size) {
            totalScoreList.add(0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        with(binding) {
            titleTv.text = intent.getStringExtra("title")
            timeTv.text = intent.getLongExtra("time", 0).toTime()
            userList.forEach{ name ->
                playerName.addSimpleView(name, WIDTH)
                totalScore.addSimpleView("0", WIDTH)
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
                        }
                        adapter.notifyDataSetChanged()
                        addScoreAdapter.notifyUserAdded()
                        totalScoreList.add(0)
                        totalScore.addSimpleView("0", WIDTH)
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
                        newList.forEachIndexed { index, value ->
                            totalScoreList[index] += value
                            val childView = totalScore.getChildAt(index) as TextView
                            childView.text = totalScoreList[index].toString()
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