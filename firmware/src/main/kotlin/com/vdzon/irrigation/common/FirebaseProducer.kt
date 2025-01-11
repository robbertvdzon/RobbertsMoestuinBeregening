package com.vdzon.irrigation.common

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.vdzon.irrigation.model.view.ViewModel

class FirebaseProducer(
    private val dbFirestore: Firestore?,
    private val collection: String,
    private val document: String,
) {
    private var lastViewModel: ViewModel? = null
    private var lastData: String? = null
    private val lastStatus: String = ""
    private var nrUpdates =0
/*
Waarom in common data? dit is niet echt common!
 */

    fun setState(viewModel: ViewModel) {
        if (lastViewModel==viewModel) return

        println("Write viewModel to firebase: $viewModel")
        val documentRef = dbFirestore?.collection(collection)?.document(document)
        documentRef?.set(mapOf("viewModel" to viewModel), SetOptions.merge())
        lastViewModel = viewModel

    }


}