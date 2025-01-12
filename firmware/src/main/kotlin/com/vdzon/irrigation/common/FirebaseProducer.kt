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
    /*
    Waarom in common module? dit is niet echt common!
     */

    fun setState(viewModel: ViewModel) {
        if (lastViewModel == viewModel) return
        val documentRef = dbFirestore?.collection(collection)?.document(document)
        documentRef?.set(mapOf("viewModel" to viewModel), SetOptions.merge())
        lastViewModel = viewModel

    }


}