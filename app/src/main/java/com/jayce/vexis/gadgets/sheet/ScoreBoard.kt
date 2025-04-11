package com.jayce.vexis.gadgets.sheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool.addSimpleView
import com.jayce.vexis.databinding.AddRecordDialogBinding
import com.jayce.vexis.databinding.AddRecordUserBinding
import com.jayce.vexis.databinding.NewPocketRecordBinding
import com.jayce.vexis.widgets.CustomDialog
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.gadgets.sheet.adapter.RecordAdapter
import com.jayce.vexis.gadgets.sheet.adapter.ScoreInsertAdapter
import com.jayce.vexis.gadgets.sheet.bean.RecordBean
import com.jayce.vexis.gadgets.sheet.bean.RecordItemBean
import com.jayce.vexis.gadgets.sheet.bean.ScoreBean
import com.jayce.vexis.gadgets.sheet.database.ScoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class ScoreBoard : AppCompatActivity() {
    companion object {
        const val WIDTH = 180
        const val HEIGHT = 180
    }

    private lateinit var binding: NewPocketRecordBinding
    private lateinit var title: String
    private lateinit var createTime: String
    private val userList = ArrayList<String>()
    private val scoreList = ArrayList<RecordBean>()
    private val totalScoreList = ArrayList<Int>()
    private val adapter by lazy{
        RecordAdapter(scoreList)
    }
    private val scoreDao by lazy {
        ScoreDatabase.getDatabase(this).recordDao()
    }
    private lateinit var scoreInsertAdapter: ScoreInsertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewPocketRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        userList.clear()
        val list = intent.getStringArrayListExtra("userData")
        Log.e("TAG","get data: $list")
        intent.getStringArrayListExtra("userData")?.let { userNames ->
            userList.addAll(
                userNames.filter { it.isNotEmpty() }.toSet()
            )
        }
        Log.e("TAG","$userList")
        repeat(userList.size) {
            totalScoreList.add(0)
        }
        scoreInsertAdapter = ScoreInsertAdapter(userList)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        with(binding) {
            title = intent.getStringExtra("title") ?: "默认标题"
            titleTv.text = title
            createTime = intent.getLongExtra("time", 0).toTime()
            timeTv.text = createTime
            userList.forEach{ name ->
                playerName.addSimpleView(name, WIDTH)
                totalScore.addSimpleView("0", WIDTH)
            }
            addUser.setOnClickListener {
                CustomDialog(
                    this@ScoreBoard,
                    AddRecordUserBinding.inflate(layoutInflater)
                ).apply{
                    setTitle("添加角色")
                    setCustomLeftButton{ _, _ ->
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
                        scoreInsertAdapter.notifyUserAdded()
                        totalScoreList.add(0)
                        totalScore.addSimpleView("0", WIDTH)
                        dismiss()
                    }
                    show()
                }
            }
            addRecord.setOnClickListener {
                CustomDialog(
                    this@ScoreBoard,
                    AddRecordDialogBinding.inflate(layoutInflater)
                ).apply {
                    val rv = viewBinding.scoreRv
                    rv.layoutManager = LinearLayoutManager(this@ScoreBoard)
                    rv.adapter = scoreInsertAdapter
                    setTitle("设置分数")
                    setCustomLeftButton { _, _ ->
                        dismiss()
                    }
                    setCustomRightButton("添加") { _, _ ->
                        val scoreList = scoreInsertAdapter.getscoreList()
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
            save.setOnClickListener {
                saveRecord()
            }
            rv.layoutManager = LinearLayoutManager(this@ScoreBoard)
            rv.adapter = adapter
            rvSC.setOnScrollChangeListener { _, i, i2, _, _ ->
                playerNameSV.scrollTo(i, i2)
                totalScoreSV.scrollTo(i, i2)
            }
            rv.setOnScrollChangeListener { _, _, _, oldScrollX, oldScrollY ->
                val scX = roundNumRV.scrollX
                val scY = roundNumRV.scrollY
                roundNumRV.scrollTo(scX - oldScrollX, scY - oldScrollY)
            }
            playerNameSV.setOnTouchListener { _, _ -> true }
            totalScoreSV.setOnTouchListener { _, _ -> true }
            roundNumRV.setOnTouchListener { _, _ -> true }
        }
    }

    private fun saveRecord() {
        lifecycleScope.launch(Dispatchers.IO) {
            val recordItemBean = RecordItemBean(title, createTime)
            val recordId = scoreDao.insertRecord(recordItemBean)
            val userStr = StringBuilder()
            val scoreStr = StringBuilder()
            val totalStr = StringBuilder()
            userList.forEachIndexed { i, v ->
                userStr.append(v)
                if(i != userList.size - 1) {
                    userStr.append("$")
                }
            }
            totalScoreList.forEachIndexed { i, v ->
                totalStr.append(v)
                if(i != totalScoreList.size - 1) {
                    totalStr.append("$")
                }
            }
            scoreList.forEachIndexed { idx, value ->
                val roundScore = value.scores
                roundScore.forEachIndexed { i, v ->
                    scoreStr.append(v)
                    if (i != roundScore.size - 1) {
                        scoreStr.append("$")
                    }
                }
                if (idx != scoreList.size - 1) {
                    scoreStr.append("&")
                }
            }
            val scoreBean = ScoreBean(recordId, userStr.toString(), scoreStr.toString(), totalStr.toString())
            scoreDao.insertScore(scoreBean)
        }
        "保存成功".toast()
    }

    private fun addRecord(list: ArrayList<Int>) {
        scoreList.add(RecordBean("${System.currentTimeMillis()}", list))
        adapter.notifyItemInserted(scoreList.size)
        binding.roundNum.addSimpleView("${scoreList.size}", HEIGHT, WIDTH)
    }
}
