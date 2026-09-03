package com.jayce.vexis.business.article

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import com.jayce.vexis.R
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.ability.diff.DiffCallback
import com.jayce.vexis.client.ability.diff.DiffChar
import com.jayce.vexis.client.ability.diff.DiffTool
import com.jayce.vexis.client.ability.diff.Differ
import com.jayce.vexis.client.ability.diff.P

object DifferHelper {

    private var renderCallback: ((SpannableString) -> Unit)? = null
    private const val SPAN_FLAG = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

    fun setRenderCallback(func: (SpannableString) -> Unit) { renderCallback = func }

    fun diff(start: List<String>, dest: List<String>) {
        val diff = DiffTool(start, dest)
        val startBuffer = arrayListOf<String>()
        val destBuffer = arrayListOf<String>()
        diff.showChange(object : DiffCallback<String> {
            override fun onSnake(i: Int, startIndex: Int, destIndex: Int, item: String) {
                startBuffer.add(item)
                destBuffer.add(item)
            }
            override fun onAdd(i: Int, index: Int, destItem: String) { destBuffer.add(destItem) }
            override fun onDelete(i: Int, index: Int, startItem: String) { startBuffer.add(startItem) }
            override fun onBlockStart(i: Int, size: Int) {
                startBuffer.clear()
                destBuffer.clear()
            }
            override fun onBlockEnd(i: Int) { showLineDiff(startBuffer, destBuffer) }
        })
    }

    private fun showLineDiff(start: List<String>, dest: List<String>) {
        val matchTrace = DiffTool.getMatchTrace(start, dest)
        var startIndex = 0
        var destIndex = 0
        var traceIndex = 0
        while (traceIndex < matchTrace.size) {
            val trace = matchTrace[traceIndex]
            startIndex = renderText(start, startIndex, trace.x, R.color.red, true)
            destIndex = renderText(dest, destIndex, trace.y, R.color.glassyGreen)
            val charList = arrayListOf<DiffChar>()
            val startStr = start[trace.x]
            val destStr = dest[trace.y]
            val matchDiff = DiffTool(startStr.toList(), destStr.toList())
            matchDiff.showChange(object : Differ<Char>() {
                override fun onAdd(i: Int, index: Int, destItem: Char) { charList.add(DiffChar(destItem, P.ADD)) }
                override fun onDelete(i: Int, index: Int, startItem: Char) { charList.add(DiffChar(startItem, P.DELETE)) }
                override fun onSnake(i: Int, startIndex: Int, destIndex: Int, item: Char) { charList.add(DiffChar(item, P.SNAKE)) }
            })
            val spannableString = modifySpanner(charList, matchDiff)
            renderCallback?.invoke(spannableString)
            startIndex++
            destIndex++
            traceIndex++
        }
        renderText(start, startIndex, start.size, R.color.red, true)
        renderText(dest, destIndex, dest.size, R.color.glassyGreen)
    }

    private fun modifySpanner(charList: List<DiffChar>, matchDiff: DiffTool<Char>): SpannableString {
        val content = charList.map { it.char }.joinToString("")
        val spannableString = SpannableString(content)
        var charIndex = 0
        matchDiff.blockTrace(charList.map { it.type }).forEach { cBlock ->
            val mean =  cBlock.average()
            if (mean == 0.0) {
                charIndex += cBlock.size
                return@forEach
            }
            var greenIndex = -1
            for (i in cBlock.indices) {
                if (cBlock[i] == P.ADD) {
                    greenIndex = charIndex + i
                    break
                }
            }
            if (greenIndex < 0) {
                spannableString.setDeleteSpan(charIndex, charIndex + cBlock.size, SPAN_FLAG)
            } else if (greenIndex == 0) {
                spannableString.setAddSpan(charIndex, charIndex + cBlock.size, SPAN_FLAG)
            } else {
                spannableString.setDeleteSpan(charIndex, greenIndex, SPAN_FLAG)
                spannableString.setAddSpan(greenIndex, charIndex + cBlock.size, SPAN_FLAG)
            }
            charIndex += cBlock.size
        }
        return spannableString
    }

    private fun renderText(list: List<String>, i: Int, endIndex: Int, color: Int, addStrike: Boolean = false): Int {
        var index = i
        while (index < endIndex) {
            val str = list[index]
            val spannableString = SpannableString(str)
            val span = ForegroundColorSpan(envContext.getColor(color))
            spannableString.setSpan(span, 0, str.length, SPAN_FLAG)
            if (addStrike) {
                val strikeSpan = StrikethroughSpan()
                spannableString.setSpan(strikeSpan, 0, str.length, SPAN_FLAG)
            }
            renderCallback?.invoke(spannableString)
            index++
        }
        return index
    }

    private fun SpannableString.setDeleteSpan(start: Int, end: Int, flag: Int) {
        val strikeSpan = StrikethroughSpan()
        val redSpan = ForegroundColorSpan(envContext.getColor(R.color.red))
        setSpan(strikeSpan, start, end, flag)
        setSpan(redSpan, start, end, flag)
    }

    private fun SpannableString.setAddSpan(start: Int, end: Int, flag: Int) {
        val greenSpan = ForegroundColorSpan(envContext.getColor(R.color.glassyGreen))
        setSpan(greenSpan, start, end, flag)
    }
}