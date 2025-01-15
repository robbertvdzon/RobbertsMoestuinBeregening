package com.vdzon.irrigation.components.log

import com.vdzon.irrigation.api.log.Log

class LogImpl : Log {
    override fun logDebug(msg: String?) {
        println(msg)
    }

    override fun logInfo(msg: String?) {
        println(msg)
    }

    override fun logWarning(msg: String?) {
        println(msg)
    }

    override fun logError(msg: String?) {
        println(msg)
    }

}
