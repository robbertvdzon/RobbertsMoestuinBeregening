package com.vdzon.irrigation.common

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.vdzon.irrigation.model.view.ViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FirebaseProducer(
    private val dbFirestore: Firestore?,
    private val collection: String,
    private val document: String,
) {
    private var lastViewModel: ViewModel? = null
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    /*
    Waarom in common module? dit is niet echt common!
     */

    fun cleanLastState(){
        lastViewModel = null
    }

    fun setState(viewModel: ViewModel) {
        if (lastViewModel == viewModel) return
        val updateDateTime = LocalDateTime.now().format(formatter)
        val documentRef = dbFirestore?.collection(collection)?.document(document)
        documentRef?.set(mapOf("viewModel" to viewModel), SetOptions.merge())
        documentRef?.set(mapOf("lastupdate" to updateDateTime), SetOptions.merge())
        lastViewModel = viewModel

    }


}