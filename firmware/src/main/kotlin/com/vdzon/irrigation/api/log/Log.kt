package com.vdzon.irrigation.api.log

interface Log {
    fun logDebug(msg: String?)
    fun logInfo(msg: String?)
    fun logWarning(msg: String?)
    fun logError(msg: String?)
}
