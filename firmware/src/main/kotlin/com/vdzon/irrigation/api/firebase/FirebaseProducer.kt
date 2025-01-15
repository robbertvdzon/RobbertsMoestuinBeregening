package com.vdzon.irrigation.api.firebase

import com.vdzon.irrigation.api.model.view.ViewModel

interface FirebaseProducer {
    fun cleanLastState()
    fun setState(viewModel: ViewModel)
}