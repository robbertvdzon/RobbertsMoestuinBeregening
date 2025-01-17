package com.vdzon.irrigation.components.firebase

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.vdzon.irrigation.api.firebase.FirebaseProducer
import com.vdzon.irrigation.api.model.view.ViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FirebaseProducerImpl(
    private val dbFirestore: Firestore?,
    private val collection: String,
    private val document: String,
    private val objectMapper: ObjectMapper
) : FirebaseProducer {
    private var lastViewModel: ViewModel? = null
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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
}