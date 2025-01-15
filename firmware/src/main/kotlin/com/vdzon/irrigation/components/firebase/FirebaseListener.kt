package com.vdzon.irrigation.components.firebase

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.vdzon.irrigation.api.commandprocessor.CommandProcessor
import java.util.concurrent.Executors

class FirebaseListener(
    private val collection: String,
    private val document: String,
    private val commandProcessor: CommandProcessor
) {
    private val executor = Executors.newSingleThreadExecutor()

    fun processCommands(dbFirestore: Firestore?) {
        val documentRef = dbFirestore?.collection(collection)?.document(document)
        documentRef?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error listening to document: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                processDocumentSnapshot(snapshot, documentRef)
            } else {
                println("Document does not exists")
            }
        }
    }

    private fun processDocumentSnapshot(
        snapshot: DocumentSnapshot,
        documentRef: DocumentReference
    ) {
        val data = snapshot.data ?: emptyMap()
        for ((key, command) in data) {
            processCommand(command.toString())
            documentRef.update(key, FieldValue.delete())
                .addListener({ }, executor)
        }
    }

    private fun processCommand(command: String) {
        try {
            commandProcessor.process(command)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

}