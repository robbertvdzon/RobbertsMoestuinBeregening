package com.vdzon.irrigation.components.commandprocessor.impl

import com.vdzon.irrigation.components.commandprocessor.api.CommandProcessor
import com.vdzon.irrigation.components.commandprocessor.api.CommandProcessorListener


class BewateringCommandProcessor : CommandProcessor {
    private var listener: CommandProcessorListener? = null

    override fun process(command: String) {
        println("Processing command: $command")
        val count = command.toIntOrNull()
        if (count!=null){
            listener?.addStopTime(count)
        }
//
//        if (command.startsWith("REQUEST_UPDATE")) {
//            controller.startUpdating()
//            return
//        }
//
    }

    fun registerListener(listener: CommandProcessorListener) {
        this.listener = listener

    }
}