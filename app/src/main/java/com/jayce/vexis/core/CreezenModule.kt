package com.jayce.vexis.core

import com.jayce.vexis.business.history.TimeManager
import com.jayce.vexis.foundation.ability.EventRepository
import com.jayce.vexis.domain.viewmodel.ChatViewModel
import com.jayce.vexis.domain.viewmodel.FileViewModel
import com.jayce.vexis.domain.viewmodel.GomokuViewModel
import com.jayce.vexis.domain.viewmodel.MailViewModel
import com.jayce.vexis.domain.viewmodel.PokerViewModel
import com.jayce.vexis.domain.viewmodel.RegisterViewModel
import com.jayce.vexis.foundation.ability.socket.LanManager
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val modules = module {

    viewModel { RegisterViewModel() }
    viewModel { ChatViewModel(get()) }
    viewModel { FileViewModel() }
    viewModel { GomokuViewModel() }
    viewModel { PokerViewModel() }
    viewModel { MailViewModel(get()) }

    single { EventRepository() }
    single { LanManager() }
    single { TimeManager() }
}