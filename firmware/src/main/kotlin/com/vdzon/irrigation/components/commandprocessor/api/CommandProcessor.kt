package com.vdzon.irrigation.components.commandprocessor.api

interface CommandProcessor {
    fun process(command: String)
}