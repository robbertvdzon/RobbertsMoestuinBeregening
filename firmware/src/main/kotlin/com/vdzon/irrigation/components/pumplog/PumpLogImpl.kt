package com.vdzon.irrigation.components.pumplog

import com.fasterxml.jackson.databind.ObjectMapper
import com.vdzon.irrigation.api.controller.Controller
import com.vdzon.irrigation.api.firebase.FirebaseProducer
import com.vdzon.irrigation.api.firebase.FirestoreNotInitialized
import com.vdzon.irrigation.api.firebase.FirestoreSnapshotNotFound
import com.vdzon.irrigation.api.log.Log
import com.vdzon.irrigation.api.pumplog.PumpLog
import com.vdzon.irrigation.api.pumplog.PumpLogItem
import com.vdzon.irrigation.api.pumplog.PumpLogState
import java.time.LocalDateTime
import kotlin.concurrent.thread

class PumpLogImpl(
    private val firebaseProducer: FirebaseProducer,
    private val log: Log,
    private val objectMapper: ObjectMapper,
    private val controller: Controller

) : PumpLog {

    override fun start() {
        thread(start = true) {
            startLogPumpUsageThread()
        }
    }

    private fun startLogPumpUsageThread() {
        while (true) {
            try {
                val now = LocalDateTime.now()
                val currentState = controller.getCurrentState()
                val pumpIsOpen = currentState.closeTime.isAfter(now)
                val area = currentState.irrigationArea
                // on every 5 minute: when the pump is open, register this at firebase
                if (now.minute % 5 == 0 && pumpIsOpen) {
                    log.logInfo("Log pump time")
                    val oldLogState = firebaseProducer.getPumpUsage(area) ?: PumpLogState(0, emptyList())
                    val updatedLogState = PumpLogState(
                        minutes = oldLogState.minutes + 5,
                        log = oldLogState.log + PumpLogItem(now.hour, now.minute)
                    )
                    firebaseProducer.setPumpUsage(updatedLogState, area)
                }
            } catch (e: FirestoreNotInitialized) {
                log.logInfo("Firestore not initialized, skip log")
            } catch (e: FirestoreSnapshotNotFound) {
                log.logInfo("Firestore notsnapshot not found, skip log")
            } catch (e: Exception) {
                e.printStackTrace()
                log.logError(e.message)
            }

            sleepOneMinute()
        }
    }

    private fun sleepOneMinute() {
        try {
            Thread.sleep(1000 * 60)// one minute
        } catch (e: Exception) {

        }
    }


}