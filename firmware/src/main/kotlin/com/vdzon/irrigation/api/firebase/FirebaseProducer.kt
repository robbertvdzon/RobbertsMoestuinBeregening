package com.vdzon.irrigation.api.firebase

import com.vdzon.irrigation.api.model.IrrigationArea
import com.vdzon.irrigation.api.model.view.ViewModel
import com.vdzon.irrigation.api.pumplog.PumpLogState

interface FirebaseProducer {
    fun cleanLastState()
    fun setState(viewModel: ViewModel)
    fun getPumpUsage(area: IrrigationArea): PumpLogState?
    fun setPumpUsage(pumpLogState: PumpLogState, area: IrrigationArea)
}

class FirestoreNotInitialized(): RuntimeException()

class FirestoreSnapshotNotFound(): RuntimeException()