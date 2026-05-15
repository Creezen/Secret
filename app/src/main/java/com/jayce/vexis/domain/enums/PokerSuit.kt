package com.jayce.vexis.domain.enums

import com.jayce.vexis.R

enum class PokerSuit(val colorId: Int, val resId: Int) {
    SPADE(R.color.black, R.drawable.spade), // 黑桃
    HEART(R.color.red, R.drawable.heart), // 红桃
    DIAMOND(R.color.red, R.drawable.diamond), // 方块
    CLUB(R.color.black, R.drawable.club), // 梅花
    SMALL_JOKER(R.color.black, -1), // 小王
    BIG_JOKER(R.color.red, -1) // 大王
}