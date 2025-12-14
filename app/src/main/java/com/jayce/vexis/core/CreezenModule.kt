package com.jayce.vexis.core

import com.jayce.vexis.foundation.repository.ProfileRepository
import com.jayce.vexis.foundation.viewmodel.ChatViewModel
import com.jayce.vexis.foundation.viewmodel.FileViewModel
import com.jayce.vexis.foundation.viewmodel.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val modules = module {

    viewModel { RegisterViewModel() }
    viewModel { ChatViewModel() }
    viewModel { FileViewModel() }

    single { ProfileRepository() }
}