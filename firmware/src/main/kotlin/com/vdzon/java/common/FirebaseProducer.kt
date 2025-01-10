package com.vdzon.java.common

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions

class FirebaseProducer(
    private val dbFirestore: Firestore?,
    private val collection: String,
    private val document: String,
) {
    private var lastData: String? = null
    private val lastStatus: String = ""
    private var nrUpdates =0
/*
Waarom in common data? dit is niet echt common!
 */
    fun setTime(time: String) {
        if (time==lastData){
            return
        }
        if (nrUpdates % 30 == 0){
            println("Start Write to firebase: $time")
        }
//        println("Write to firebase: $time")
        val documentRef = dbFirestore?.collection(collection)?.document(document)
        documentRef?.set(mapOf("klok" to time), SetOptions.merge())
        nrUpdates++
        documentRef?.set(mapOf("updatecount" to nrUpdates), SetOptions.merge())
        if (nrUpdates % 30 == 0){
            println("written to firebase: $time")
        }
        lastData=time
    }

    fun setStatus(status: String) {
        if (status==lastStatus){
            return
        }
        println("Write to firebase: $status")
        val documentRef = dbFirestore?.collection(collection)?.document(document)
        documentRef?.set(mapOf("status" to status), SetOptions.merge())
        nrUpdates++
        documentRef?.set(mapOf("updatecount" to nrUpdates), SetOptions.merge())
    }


}