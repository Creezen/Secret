package com.jayce.vexis.core

import com.jayce.vexis.domain.EventRepository
import com.jayce.vexis.domain.viewmodel.ChatViewModel
import com.jayce.vexis.domain.viewmodel.FileViewModel
import com.jayce.vexis.domain.viewmodel.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val modules = module {

    viewModel { RegisterViewModel() }
    viewModel { ChatViewModel(get()) }
    viewModel { FileViewModel() }

    single { EventRepository() }
}