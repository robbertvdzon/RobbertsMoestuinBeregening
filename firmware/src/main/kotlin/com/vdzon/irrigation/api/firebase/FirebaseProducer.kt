package com.vdzon.irrigation.api.firebase

import com.vdzon.irrigation.api.model.IrrigationArea
import com.vdzon.irrigation.api.model.view.ViewModel
import com.vdzon.irrigation.api.pumplog.PumpLogState
import com.vdzon.irrigation.api.pumplog.SummaryPumpUsage

interface FirebaseProducer {
    fun cleanLastState()
    fun setState(viewModel: ViewModel)
    fun getMonthlyPumpUsage(area: IrrigationArea): PumpLogState?
    fun setMonthlyPumpUsage(pumpLogState: PumpLogState, area: IrrigationArea)
    fun getSummaryPumpUsage(): SummaryPumpUsage?
    fun setSummaryPumpUsage(totalPumpLogState: SummaryPumpUsage)
}

class FirestoreNotInitialized(): RuntimeException()

class FirestoreSnapshotNotFound(): RuntimeException()