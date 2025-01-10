package com.vdzon.irrigation.components.controller

import com.vdzon.irrigation.common.CommandProcessor


class BewateringCommandProcessor(val controller: Controller) : CommandProcessor {
    override fun process(command: String) {
        println("Processing command: $command")

        if (command.startsWith("REQUEST_UPDATE")){
            controller.startUpdating()
            return
        }

        val count = command.toIntOrNull()
        if (count!=null && count>0){
            println("open $count minuten meer")
            controller.encoderUp(count)
        }
        if (count!=null && count<0){
            println("open $count minuten minder")
            controller.encoderDown(count*-1)
        }
        if (command == "open") {
            controller.openKlep()
        } else if (command == "close") {
            controller.closeKlep()
        }
    }
}