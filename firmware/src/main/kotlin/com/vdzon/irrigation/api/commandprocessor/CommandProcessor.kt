package com.vdzon.irrigation.api.commandprocessor

interface CommandProcessor {
    fun process(command: String)
}