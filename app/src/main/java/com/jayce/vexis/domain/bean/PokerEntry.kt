package com.jayce.vexis.domain.bean

import com.jayce.vexis.domain.enums.PokerSuit

data class PokerEntry(
    val suit: PokerSuit,
    val rank: Int,
) {
    var isSelect: Boolean = false

    val rankChar = when (suit) {
        PokerSuit.SMALL_JOKER -> "j\no\nk\ne\nr"
        PokerSuit.BIG_JOKER -> "J\nO\nK\nE\nR"
        else -> {
            when (rank) {
                1 -> "A"
                11 -> "J"
                12 -> "Q"
                13 -> "K"
                else -> rank.toString()
            }
        }
    }

    fun isJoker(): Boolean {
        return suit == PokerSuit.SMALL_JOKER || suit == PokerSuit.BIG_JOKER
    }
}