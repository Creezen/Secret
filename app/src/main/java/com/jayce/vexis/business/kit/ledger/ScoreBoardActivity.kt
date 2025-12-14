package com.jayce.vexis.business.kit.ledger

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.addSimpleView
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
import com.jayce.vexis.business.kit.ledger.adapter.RecordAdapter
import com.jayce.vexis.business.kit.ledger.adapter.ScoreInsertAdapter
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.AddRecordDialogBinding
import com.jayce.vexis.databinding.AddRecordUserBinding
import com.jayce.vexis.databinding.DialogBinding
import com.jayce.vexis.databinding.NewPocketRecordBinding
import com.jayce.vexis.foundation.bean.RecordEntry
import com.jayce.vexis.foundation.bean.RecordItemEntry
import com.jayce.vexis.foundation.bean.ScoreEntry
import com.jayce.vexis.foundation.database.ledger.ScoreDatabase
import com.jayce.vexis.foundation.view.block.FlexibleDialog
import kotlinx.coroutines.Dispatchers

class ScoreBoardActivity : BaseActivity<NewPocketRecordBinding>() {

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 180
    }

    private lateinit var title: String
    private lateinit var createTime: String
    private val userList = ArrayList<String>()
    private val scoreList = ArrayList<RecordEntry>()
    private val totalScoreList = ArrayList<Int>()
    private val adapter by lazy {
        RecordAdapter(scoreList)
    }
    private val scoreDao by lazy {
        ScoreDatabase.getDatabase(this).recordDao()
    }
    private lateinit var scoreInsertAdapter: ScoreInsertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        userList.clear()
        val list = intent.getStringArrayListExtra("userData")
        Log.e("TAG", "get data: $list")
        intent.getStringArrayListExtra("userData")?.let { userNames ->
            userList.addAll(
                userNames.filter { it.isNotEmpty() }.toSet(),
            )
        }
        Log.e("TAG", "$userList")
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
            userList.forEach { name ->
                playerName.addSimpleView(name, WIDTH)
                totalScore.addSimpleView("0", WIDTH)
            }
            addUser.setOnClickListener {
                FlexibleDialog<DialogBinding>(this@ScoreBoardActivity, layoutInflater)
                    .flexibleView(AddRecordUserBinding.inflate(layoutInflater))
                    .title("添加角色")
                    .negative { return@negative -1 }
                    .positive("添加") {
                        val edit = findViewById<EditText>(R.id.edit)
                        val addUserName = edit.msg()
                        playerName.addSimpleView(addUserName, WIDTH)
                        userList.add(addUserName)
                        scoreList.forEach {
                            it.scores.add(0)
                        }
                        adapter.notifyDataSetChanged()
                        scoreInsertAdapter.notifyUserAdded()
                        totalScoreList.add(0)
                        totalScore.addSimpleView("0", WIDTH)
                        return@positive -1
                    }.show()
            }
            addRecord.setOnClickListener {
                FlexibleDialog<DialogBinding>(this@ScoreBoardActivity, layoutInflater)
                    .flexibleView(AddRecordDialogBinding.inflate(layoutInflater)) {
                        scoreRv.layoutManager = LinearLayoutManager(this@ScoreBoardActivity)
                        scoreRv.adapter = scoreInsertAdapter
                    }
                    .positive("添加") {
                        val scoreList = scoreInsertAdapter.getscoreList()
                        val newList = ArrayList<Int>()
                        newList.addAll(scoreList)
                        if (newList.sum() != 0) {
                            "分数设置不合法，请检查一下哦".toast()
                            return@positive -1
                        }
                        newList.forEachIndexed { index, value ->
                            totalScoreList[index] += value
                            val childView = totalScore.getChildAt(index) as TextView
                            childView.text = totalScoreList[index].toString()
                        }
                        addRecord(newList)
                        return@positive -1
                    }.title("设置分数").show()
            }
            save.setOnClickListener {
                saveRecord()
            }
            rv.layoutManager = LinearLayoutManager(this@ScoreBoardActivity)
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
        ThreadTool.runOnMulti(Dispatchers.IO) {
            val recordItemEntry = RecordItemEntry(title, createTime)
            val recordId = scoreDao.insertRecord(recordItemEntry)
            val userStr = StringBuilder()
            val scoreStr = StringBuilder()
            val totalStr = StringBuilder()
            userList.forEachIndexed { i, v ->
                userStr.append(v)
                if (i != userList.size - 1) {
                    userStr.append("$")
                }
            }
            totalScoreList.forEachIndexed { i, v ->
                totalStr.append(v)
                if (i != totalScoreList.size - 1) {
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
            val scoreEntry = ScoreEntry(recordId, userStr.toString(), scoreStr.toString(), totalStr.toString())
            scoreDao.insertScore(scoreEntry)
        }
        "保存成功".toast()
    }

    private fun addRecord(list: ArrayList<Int>) {
        scoreList.add(RecordEntry("${System.currentTimeMillis()}", list))
        adapter.notifyItemInserted(scoreList.size)
        binding.roundNum.addSimpleView("${scoreList.size}", HEIGHT, WIDTH)
    }
}