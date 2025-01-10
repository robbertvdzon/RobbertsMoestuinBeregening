package com.vdzon.irrigation.components.log

import java.util.logging.Logger

class Log {
    /**
     * Logger instance
     */
    private val logger = Logger.getLogger(javaClass.name)


    fun logDebug(msg: String?) {
        logger.fine { msg }
    }

    fun logInfo(msg: String?) {
        logger.info { msg }
    }

    fun logWarning(msg: String?) {
        logger.warning { msg }
    }

    fun logError(msg: String?) {
        logger.severe { msg }
    }


}
