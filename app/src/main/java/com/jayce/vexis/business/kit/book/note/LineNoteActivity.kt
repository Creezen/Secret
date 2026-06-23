package com.jayce.vexis.business.kit.book.note

import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.client.AndroidTool.addSimpleView
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.BookLineNoteBinding
import com.jayce.vexis.databinding.BookNoteDialogLineBinding
import com.jayce.vexis.databinding.BookNoteDialogUserBinding
import com.jayce.vexis.domain.bean.book.BookEntry
import com.jayce.vexis.domain.bean.book.BookLineEntry
import com.jayce.vexis.domain.bean.LineEntry
import com.jayce.vexis.domain.database.book.BookDatabase
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import com.jayce.vexis.util.toTime

class LineNoteActivity : BaseActivity<BookLineNoteBinding>() {

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 100
    }

    private lateinit var title: String
    private lateinit var createTime: String
    private val playerList = ArrayList<String>()
    private val lineList = ArrayList<LineEntry>()
    private val sumList = ArrayList<Int>()
    private val lineAdapter by lazy { LineAdapter(lineList) }
    private val scoreDao by lazy { BookDatabase.getDatabase(this).recordDao() }
    private lateinit var noteDialogAdapter: NoteDialogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        val list = intent.getStringArrayListExtra("players") ?: listOf("Player1", "Player2")
        playerList.clear()
        playerList.addAll(list)
        repeat(playerList.size) { sumList.add(0) }
        noteDialogAdapter = NoteDialogAdapter(playerList)
    }

    private fun initView() = binding.apply {
        title = intent.getStringExtra("title") ?: "默认标题"
        bookTitle.text = title
        createTime = intent.getLongExtra("time", System.currentTimeMillis()).toTime()
        bookTime.text = createTime
        val initScoreList = arrayListOf<Int>()
        playerList.forEach { name ->
            playerName.addSimpleView(name, WIDTH, MATCH_PARENT)
            bookSum.addSimpleView("0", WIDTH, MATCH_PARENT)
            initScoreList.add(0)
        }
        addRecord(initScoreList)
        addUser.setOnClickListener {
            FlexibleDialog
                .flexibleView<BookNoteDialogUserBinding>(this@LineNoteActivity)
                .title("添加角色")
                .negative()
                .positive("添加") {
                    val addUserName = edit.msg()
                    playerName.addSimpleView(addUserName, WIDTH, MATCH_PARENT)
                    playerList.add(addUserName)
                    lineList.forEach { it.scores.add(0) }
                    lineAdapter.notifyDataSetChanged()
                    noteDialogAdapter.notifyUserAdded()
                    sumList.add(0)
                    bookSum.addSimpleView("0", WIDTH, MATCH_PARENT)
                }.show()
        }
        addRecord.setOnClickListener {
            FlexibleDialog
                .flexibleView<BookNoteDialogLineBinding>(this@LineNoteActivity) {
                    scoreRv.layoutManager = LinearLayoutManager(this@LineNoteActivity)
                    scoreRv.adapter = noteDialogAdapter
                }
                .positive("添加") {
                    val scoreList = noteDialogAdapter.getLineList()
                    val newList = ArrayList<Int>()
                    newList.addAll(scoreList)
                    if (newList.sum() != 0) {
                        "分数设置不合法，请检查一下哦".toast()
                    }
                    newList.forEachIndexed { index, value ->
                        sumList[index] += value
                        val childView = bookSum.getChildAt(index) as TextView
                        childView.text = sumList[index].toString()
                    }
                    addRecord(newList)
                }
                .title("设置分数")
                .show()
        }
        save.setOnClickListener {
            ThreadTool.runOnMulti{ saveRecord() }
        }
        lineRv.layoutManager = LinearLayoutManager(this@LineNoteActivity)
        lineRv.adapter = lineAdapter
        bookDataWrapper.setOnScrollChangeListener { _, i, i2, _, _ ->
            playerWrapper.scrollTo(i, i2)
            sumWrapper.scrollTo(i, i2)
        }
        lineRv.setOnScrollChangeListener { _, _, _, oldScrollX, oldScrollY ->
            val scX = stageWrapper.scrollX
            val scY = stageWrapper.scrollY
            stageWrapper.scrollTo(scX - oldScrollX, scY - oldScrollY)
        }
        playerWrapper.setOnTouchListener { _, _ -> true }
        sumWrapper.setOnTouchListener { _, _ -> true }
        stageWrapper.setOnTouchListener { _, _ -> true }
    }

    private suspend fun saveRecord() {
        val bookLineEntry = BookLineEntry(title, createTime)
        val recordId = scoreDao.insertRecord(bookLineEntry)
        val userStr = StringBuilder()
        val scoreStr = StringBuilder()
        val totalStr = StringBuilder()
        playerList.forEachIndexed { i, v ->
            userStr.append(v)
            if (i != playerList.size - 1) {
                userStr.append("$")
            }
        }
        sumList.forEachIndexed { i, v ->
            totalStr.append(v)
            if (i != sumList.size - 1) {
                totalStr.append("$")
            }
        }
        lineList.forEachIndexed { idx, value ->
            val roundScore = value.scores
            roundScore.forEachIndexed { i, v ->
                scoreStr.append(v)
                if (i != roundScore.size - 1) {
                    scoreStr.append("$")
                }
            }
            if (idx != lineList.size - 1) {
                scoreStr.append("&")
            }
        }
        val bookEntry = BookEntry(recordId, userStr.toString(), scoreStr.toString(), totalStr.toString())
        scoreDao.insertScore(bookEntry)
        ui { "保存成功".toast() }
    }

    private fun addRecord(list: ArrayList<Int>) {
        lineList.add(LineEntry("${System.currentTimeMillis()}", list))
        lineAdapter.notifyItemInserted(lineList.size)
        binding.stage.addSimpleView("${lineList.size}", MATCH_PARENT, HEIGHT)
    }
}