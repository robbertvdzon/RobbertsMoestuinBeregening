package com.vdzon.java.common

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileInputStream

class FirebaseConfig(
    private val serviceAccountFile: String,
    private val databaseUrl: String
) {
    fun initializeFirestore(): Firestore? {
        val serviceAccount =
            FileInputStream(serviceAccountFile)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(databaseUrl)
            .build()
        FirebaseApp.initializeApp(options)
        return FirestoreClient.getFirestore()
    }
}