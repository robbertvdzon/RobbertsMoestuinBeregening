package com.vdzon.irrigation.components.hardware

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.*
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalInputProvider
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider
import com.vdzon.irrigation.api.hardware.Button
import com.vdzon.irrigation.api.hardware.ButtonListener
import com.vdzon.irrigation.api.hardware.Hardware
import com.vdzon.irrigation.api.hardware.Led
import com.vdzon.irrigation.api.log.Log
import com.vdzon.irrigation.api.model.IrrigationArea
import java.util.*
import kotlin.concurrent.thread


class HardwareImpl(
    private val log: Log
) : Hardware {
    private lateinit var pi4j: Context
    private lateinit var lcd: LcdDisplay

    private val digitalOutputs: MutableMap<DIGITAL_OUTPUT, DigitalOutputWithState> = mutableMapOf()
    private var buttonListener: ButtonListener? = null
    private val requiredDisplayLines: MutableMap<Int, String?> = mutableMapOf()
    private val currentDisplayLines: MutableMap<Int, String?> = mutableMapOf()

    override fun setPump(on: Boolean) {
        val out = digitalOutputs[DIGITAL_OUTPUT.PUMP] ?: return
        out.requiredValue = on
    }

    override fun setArea(area: IrrigationArea) {
        val out = digitalOutputs[DIGITAL_OUTPUT.AREA] ?: return
        out.requiredValue = area == IrrigationArea.MOESTUIN
    }

    override fun setLedState(led: Led, on: Boolean) {
        val out = when (led) {
            Led.PUMP_OFF -> digitalOutputs[DIGITAL_OUTPUT.PUMP_OFF_LED]
            Led.PUMP_ON -> digitalOutputs[DIGITAL_OUTPUT.PUMP_ON_LED]
            Led.MOESTUIN_AREA -> digitalOutputs[DIGITAL_OUTPUT.MOESTUIN_LED]
            Led.GAZON_AREA -> digitalOutputs[DIGITAL_OUTPUT.GAZON_LED]
        }
        if (out != null) {
            out.requiredValue = on
        }
    }

    override fun displayLine(lineNr: Int, line: String) {
        requiredDisplayLines[lineNr] = line
    }

    override fun registerSwitchListener(switchListener: ButtonListener) {
        this.buttonListener = switchListener
    }

    override fun start() {
        thread(start = true) {
            startHardware()
        }
    }

    private fun startHardware() {
        pi4j = buildPi4j()
        Runtime.getRuntime().addShutdownHook(Thread {
            log.logInfo("Releasing GPIO resources...")
            pi4j.shutdown()
        })
        lcd = LcdDisplay(pi4j, 4, 20, log)
        val dout: GpioDDigitalOutputProvider = pi4j.dout()
        val din: GpioDDigitalInputProvider = pi4j.din()
        val stateChangeLogger = DigitalStateChangeListener { var1 -> log.logInfo(var1.toString()) }

        val pumpDigitalOutput: DigitalOutput = dout.create(PUMP_SOLENOID_PIN)
        pumpDigitalOutput.config().shutdownState(DigitalState.LOW)
        pumpDigitalOutput.addListener(stateChangeLogger)
        digitalOutputs.put(
            DIGITAL_OUTPUT.PUMP,
            DigitalOutputWithState(pumpDigitalOutput, false, true)
        )

        val areaDigitalOutput: DigitalOutput = dout.create(AREA_SOLENOID_PIN)
        areaDigitalOutput.config().shutdownState(DigitalState.LOW)
        areaDigitalOutput.addListener(stateChangeLogger)
        digitalOutputs.put(
            DIGITAL_OUTPUT.AREA,
            DigitalOutputWithState(areaDigitalOutput, false, true)
        )

        val pumpOnLedDigitalOutput: DigitalOutput = dout.create(PUMP_ON_LED_PIN)
        pumpOnLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        pumpOnLedDigitalOutput.addListener(stateChangeLogger)
        digitalOutputs.put(
            DIGITAL_OUTPUT.PUMP_ON_LED,
            DigitalOutputWithState(pumpOnLedDigitalOutput, true, true)
        )

        val pumpOffLedDigitalOutput: DigitalOutput = dout.create(PUMP_OFF_LED_PIN)
        pumpOffLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        pumpOffLedDigitalOutput.addListener(stateChangeLogger)
        digitalOutputs.put(
            DIGITAL_OUTPUT.PUMP_OFF_LED,
            DigitalOutputWithState(pumpOffLedDigitalOutput, true, true)
        )

        val moestuinLedDigitalOutput: DigitalOutput = dout.create(GROENTETUIN_LED_PIN)
        moestuinLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        moestuinLedDigitalOutput.addListener(stateChangeLogger)
        digitalOutputs.put(
            DIGITAL_OUTPUT.MOESTUIN_LED,
            DigitalOutputWithState(moestuinLedDigitalOutput, true, true)
        )

        val gazonLedDigitalOutput: DigitalOutput = dout.create(GAZON_LED_PIN)
        gazonLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        gazonLedDigitalOutput.addListener(stateChangeLogger)
        digitalOutputs.put(
            DIGITAL_OUTPUT.GAZON_LED,
            DigitalOutputWithState(gazonLedDigitalOutput, true, true)
        )

        createButton("moestuin_button", MOESTUIN_BUTTON_PIN, din) {
            if (it.state() === DigitalState.LOW) buttonListener?.onButtonClick(Button.MOESTUIN_AREA)
        }
        createButton("gazon_button", GAZON_BUTTON_PIN, din) {
            if (it.state() === DigitalState.LOW) buttonListener?.onButtonClick(Button.GAZON_AREA)
        }
        createButton("add_5_button", PLUS_5_MINUTES_BUTTON_PIN, din) {
            if (it.state() === DigitalState.LOW) buttonListener?.onButtonClick(Button.PLUS_5_MINUTES)
        }
        createButton("min_5_button", MIN_5_MINUTES_BUTTON_PIN, din) {
            if (it.state() === DigitalState.LOW) buttonListener?.onButtonClick(Button.MIN_5_MINUTES)
        }

        startDisplayAndPinOutThread()
    }

    private fun createButton(
        id: String,
        pinButton: Int,
        din: GpioDDigitalInputProvider,
        function: (DigitalStateChangeEvent<Digital<*, *, *>>) -> Unit
    ): DigitalInput {
        val properties = Properties()
        properties["id"] = id
        properties["address"] = pinButton
        properties["pull"] = "DOWN"
        properties["name"] = id.uppercase()
        val button = din.create(
            DigitalInput.newConfigBuilder(pi4j)
                .load(properties)
                .build()
        )
        button.addListener(function)
        return button
    }

    private fun buildPi4j(): Context = Pi4J.newAutoContext()

    private fun startDisplayAndPinOutThread() = thread(start = true) {
        displayAndPinOutThread()
    }

    private fun displayAndPinOutThread() {
        lcd.setDisplayBacklight(true)
        lcd.clearDisplay()
        while (true) {
            try {
                updateDisplay()
                updatePinOut()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sleep()
        }
    }

    private fun updateDisplay() {
        (1..4).forEach { lineNr ->
            val currentLine = currentDisplayLines[lineNr]
            val requiredLine = requiredDisplayLines[lineNr]
            if (currentLine != requiredLine) {
                val line = requiredLine ?: ""
                lcd.displayText(line, lineNr)
                currentDisplayLines[lineNr] = requiredLine
            }
        }
    }

    private fun updatePinOut() {
        // check all pin's if they have the required state
        digitalOutputs.values.forEach {
            if (it.currentValue != it.requiredValue) {
                it.digitalOutput.setState(it.requiredValue)
                it.currentValue = it.digitalOutput.isHigh
            }
        }
    }

    private fun sleep() {
        try {
            Thread.sleep(10)
        } catch (e: InterruptedException) {
        }
    }

    companion object {
        private const val MIN_5_MINUTES_BUTTON_PIN = 17
        private const val PLUS_5_MINUTES_BUTTON_PIN = 4
        private const val MOESTUIN_BUTTON_PIN = 22
        private const val GAZON_BUTTON_PIN = 27

        private const val PUMP_ON_LED_PIN = 13
        private const val PUMP_OFF_LED_PIN = 10
        private const val GROENTETUIN_LED_PIN = 6
        private const val GAZON_LED_PIN = 11

        private const val PUMP_SOLENOID_PIN = 19
        private const val AREA_SOLENOID_PIN = 26
    }
}

private data class DigitalOutputWithState(
    var digitalOutput: DigitalOutput,
    var requiredValue: Boolean,
    var currentValue: Boolean?,
)

private enum class DIGITAL_OUTPUT {
    AREA,
    PUMP,
    PUMP_ON_LED,
    PUMP_OFF_LED,
    MOESTUIN_LED,
    GAZON_LED
}