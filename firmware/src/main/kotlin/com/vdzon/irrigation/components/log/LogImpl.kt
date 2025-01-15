package com.vdzon.irrigation.components.log

import com.vdzon.irrigation.api.log.Log
import java.util.logging.Logger

class LogImpl : Log {
    private val logger = Logger.getLogger(javaClass.name)

    override fun logDebug(msg: String?) {
        logger.fine { msg }
    }

    override fun logInfo(msg: String?) {
        logger.info { msg }
    }

    override fun logWarning(msg: String?) {
        logger.warning { msg }
    }

    override fun logError(msg: String?) {
        logger.severe { msg }
    }

}
