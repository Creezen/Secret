package com.jayce.vexis.business.kit.poker.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.PokerDialogCheatFragmentBinding

class CheatFragment : BaseFragment<PokerDialogCheatFragmentBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}