package com.vdzon.irrigation.components.firebase

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.vdzon.irrigation.api.firebase.FirebaseProducer
import com.vdzon.irrigation.api.firebase.FirestoreNotInitialized
import com.vdzon.irrigation.api.firebase.FirestoreSnapshotNotFound
import com.vdzon.irrigation.api.model.IrrigationArea
import com.vdzon.irrigation.api.model.view.ViewModel
import com.vdzon.irrigation.api.pumplog.PumpLogState
import com.vdzon.irrigation.api.pumplog.SummaryPumpUsage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FirebaseProducerImpl(
    private val dbFirestore: Firestore?,
    private val collection: String,
    private val document: String,
    private val logDocument: String,
    private val summaryDocument: String,
    private val objectMapper: ObjectMapper
) : FirebaseProducer {
    private var lastViewModel: ViewModel? = null
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    override fun cleanLastState() {
        lastViewModel = null
    }

    override fun setState(viewModel: ViewModel) {
        if (lastViewModel == viewModel) return
        val updateDateTime = LocalDateTime.now().format(formatter)
        val documentRef = dbFirestore?.collection(collection)?.document(document)
        val jsonModel = objectMapper.writeValueAsString(viewModel)
        documentRef?.set(mapOf("viewModel" to jsonModel), SetOptions.merge())
        documentRef?.set(mapOf("lastupdate" to updateDateTime), SetOptions.merge())
        lastViewModel = viewModel
    }

    override fun getMonthlyPumpUsage(area: IrrigationArea): PumpLogState? {
        val documentRef = dbFirestore?.collection(collection)?.document(logDocument) ?: throw FirestoreNotInitialized()
        val snapshot = documentRef.get().get() ?: throw FirestoreSnapshotNotFound()
        val data = snapshot.data ?: emptyMap()
        val key = logKey(area)
        val pumpLogStateJson = data[key] ?: return null
        val readValue = objectMapper.readValue(pumpLogStateJson.toString(), PumpLogState::class.java)
        return readValue
    }


    override fun setMonthlyPumpUsage(pumpLogState: PumpLogState, area: IrrigationArea) {
        val key = logKey(area)
        val documentRef = dbFirestore?.collection(collection)?.document(logDocument)
        val jsonModel = objectMapper.writeValueAsString(pumpLogState)
        documentRef?.set(mapOf(key to jsonModel), SetOptions.merge())
    }

    private fun logKey(area: IrrigationArea): String {
        val now = LocalDateTime.now()
        val yearMonth = now.format(yearMonthFormatter)
        val key = "$yearMonth ${area.name}"
        return key
    }

    override fun getSummaryPumpUsage(): SummaryPumpUsage? {
        val documentRef = dbFirestore?.collection(collection)?.document(summaryDocument) ?: throw FirestoreNotInitialized()
        val snapshot = documentRef.get().get() ?: throw FirestoreSnapshotNotFound()
        val data = snapshot.data ?: emptyMap()
        val summaryPumpLogUsage = data["summary"] ?: return null
        return objectMapper.readValue(summaryPumpLogUsage.toString(), SummaryPumpUsage::class.java)
    }

    override fun setSummaryPumpUsage(totalPumpLogState: SummaryPumpUsage) {
        val documentRef = dbFirestore?.collection(collection)?.document(summaryDocument)
        val jsonModel = objectMapper.writeValueAsString(totalPumpLogState)
        documentRef?.set(mapOf("summary" to jsonModel), SetOptions.merge())


    }


}