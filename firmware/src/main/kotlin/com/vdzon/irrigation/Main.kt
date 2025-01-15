package com.vdzon.irrigation

import com.vdzon.irrigation.common.FirebaseConfig
import com.vdzon.irrigation.common.FirebaseListener
import com.vdzon.irrigation.common.FirebaseProducer
import com.vdzon.irrigation.components.commandprocessor.impl.BewateringCommandProcessor
import com.vdzon.irrigation.components.controller.Controller
import com.vdzon.irrigation.components.controller.ControllerImpl
import com.vdzon.irrigation.components.hardware.impl.HardwareImpl
import com.vdzon.irrigation.components.hardware.simulation.HardwareSimulation
import com.vdzon.irrigation.components.log.Log
import com.vdzon.irrigation.components.network.api.Network
import com.vdzon.irrigation.components.network.impl.NetworkImpl

object Main {

    private const val SERVICE_ACCOUNT_FILE_OSX =
        "/Users/robbert/tuinbewatering-firebase-adminsdk-mdooy-b394f2553c.json" // change to robbertvdzon op eigen mac
    private const val SERVICE_ACCOUNT_FILE_LINUX =
        "/home/robbert/tuinbewatering-firebase-adminsdk-mdooy-b394f2553c.json"
    private const val DATABASE_URL = "https://tuinbewatering.firebaseio.com"
    private const val COLLECTION = "bewatering"
    private const val COMMANDS_DOCUMENT = "commands"
    private const val STATUS_DOCUMENT = "status"

    @JvmStatic
    fun main(args: Array<String>) {
        val log = Log()
        val development = System.getProperty("os.name").contains("OS X")
        val hardware = if (development) HardwareSimulation() else HardwareImpl(log)
        val network: Network = NetworkImpl()
        val serviceAccountFile = if (development) SERVICE_ACCOUNT_FILE_OSX else SERVICE_ACCOUNT_FILE_LINUX
        val firebaseConfig = FirebaseConfig(serviceAccountFile, DATABASE_URL)
        val dbFirestore = firebaseConfig.initializeFirestore()
        val firebaseProducer = FirebaseProducer(dbFirestore, COLLECTION, STATUS_DOCUMENT)
        val controller: Controller = ControllerImpl(hardware, firebaseProducer)
        val commandProcessor = BewateringCommandProcessor()
        val firebaseListener = FirebaseListener(COLLECTION, COMMANDS_DOCUMENT, commandProcessor)

        firebaseListener.processCommands(dbFirestore)
        hardware.registerSwitchListener(controller)
        commandProcessor.registerListener(controller)
        network.registerNetworkListener(controller)
        hardware.start()
        network.start()
        controller.start()
    }



}
