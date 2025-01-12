package com.vdzon.irrigation.components.hardware.impl

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.*
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalInputProvider
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider
import com.pi4j.util.Console
import com.vdzon.irrigation.components.log.Log
import com.vdzon.irrigation.components.hardware.api.*
import com.vdzon.irrigation.model.IrrigationArea
import java.util.*
import kotlin.concurrent.thread


class HardwareImpl(val log: Log) : Hardware {
    lateinit var pi4j: Context
    lateinit var lcd: LcdDisplay
    lateinit var areaDigitalOutput: DigitalOutput
    lateinit var pumpDigitalOutput: DigitalOutput
    lateinit var pumpOnLedDigitalOutput: DigitalOutput
    lateinit var pumpOffLedDigitalOutput: DigitalOutput
    lateinit var moestuinLedDigitalOutput: DigitalOutput
    lateinit var gazonLedDigitalOutput: DigitalOutput

    private var buttonListener: ButtonListener? = null
    private val requiredDisplayLines: MutableMap<Int, String?> = mutableMapOf()
    private val currentDisplayLines: MutableMap<Int, String?> = mutableMapOf()

    override fun setPump(on: Boolean) {
        when {
            on -> if (!pumpDigitalOutput.isHigh) pumpDigitalOutput.high()
            !on -> if (!pumpDigitalOutput.isLow) pumpDigitalOutput.low()
        }
    }

    override fun setArea(area: IrrigationArea) {
        when (area) {
            IrrigationArea.MOESTUIN -> if (!areaDigitalOutput.isHigh) areaDigitalOutput.high()
            IrrigationArea.GAZON -> if (!areaDigitalOutput.isLow) areaDigitalOutput.low()
        }
    }

    override fun setLedState(led: Led, on: Boolean) {
        when {
            led == Led.PUMP_OFF && on -> if (!pumpOffLedDigitalOutput.isHigh) pumpOffLedDigitalOutput.high()
            led == Led.PUMP_OFF && !on -> if (!pumpOffLedDigitalOutput.isLow) pumpOffLedDigitalOutput.low()

            led == Led.PUMP_ON && on -> if (!pumpOnLedDigitalOutput.isHigh) pumpOnLedDigitalOutput.high()
            led == Led.PUMP_ON && !on -> if (!pumpOnLedDigitalOutput.isLow) pumpOnLedDigitalOutput.low()

            led == Led.MOESTUIN_AREA && on -> if (!moestuinLedDigitalOutput.isHigh) moestuinLedDigitalOutput.high()
            led == Led.MOESTUIN_AREA && !on -> if (!moestuinLedDigitalOutput.isLow) moestuinLedDigitalOutput.low()

            led == Led.GAZON_AREA && on -> if (!gazonLedDigitalOutput.isHigh) gazonLedDigitalOutput.high()
            led == Led.GAZON_AREA && !on -> if (!gazonLedDigitalOutput.isLow) gazonLedDigitalOutput.low()
        }
    }

    override fun displayLine(lineNr: Int, line: String) {
        requiredDisplayLines.put(lineNr, line)
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
            println("Releasing GPIO resources...")
            pi4j.shutdown()
        })
        lcd = LcdDisplay(pi4j, 4, 20, log)
        printInfo()

        val dout: GpioDDigitalOutputProvider = pi4j.dout()
        val din: GpioDDigitalInputProvider = pi4j.din()

        pumpDigitalOutput = dout.create(PUMP_SOLENOID_PIN)
        pumpDigitalOutput.config().shutdownState(DigitalState.LOW)
        pumpDigitalOutput.addListener(System.out::println)
        pumpDigitalOutput.low()

        areaDigitalOutput = dout.create(AREA_SOLENOID_PIN)
        areaDigitalOutput.config().shutdownState(DigitalState.LOW)
        areaDigitalOutput.addListener(System.out::println)
        areaDigitalOutput.low()

        pumpOnLedDigitalOutput = dout.create(PUMP_ON_LED_PIN)
        pumpOnLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        pumpOnLedDigitalOutput.addListener(System.out::println)
        pumpOnLedDigitalOutput.high()

        pumpOffLedDigitalOutput = dout.create(PUMP_OFF_LED_PIN)
        pumpOffLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        pumpOffLedDigitalOutput.addListener(System.out::println)
        pumpOffLedDigitalOutput.high()

        moestuinLedDigitalOutput = dout.create(GROENTETUIN_LED_PIN)
        moestuinLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        moestuinLedDigitalOutput.addListener(System.out::println)
        moestuinLedDigitalOutput.high()

        gazonLedDigitalOutput = dout.create(GAZON_LED_PIN)
        gazonLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        gazonLedDigitalOutput.addListener(System.out::println)
        gazonLedDigitalOutput.high()

        createButton("moestuin_button", MOESTUIN_BUTTON_PIN, din) {
            buttonListener?.onButtonClick(Button.MOESTUIN_AREA)
        }
        createButton("gazon_button", GAZON_BUTTON_PIN, din) {
            buttonListener?.onButtonClick(Button.GAZON_AREA)
        }
        createButton("add_5_button", PLUS_5_MINUTES_BUTTON_PIN, din) {
            buttonListener?.onButtonClick(Button.PLUS_5_MINUTES)
        }
        createButton("min_5_button", MIN_5_MINUTES_BUTTON_PIN, din) {
            buttonListener?.onButtonClick(Button.MIN_5_MINUTES)
        }

        startDisplayThread()
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

    private fun printInfo() {
        val console = Console()
        console.title("<-- The Pi4J Project -->", "Minimal Example project")
        PrintInfo.printLoadedPlatforms(console, pi4j)
        PrintInfo.printDefaultPlatform(console, pi4j)
        PrintInfo.printProviders(console, pi4j)
        PrintInfo.printRegistry(console, pi4j)
    }

    fun startDisplayThread() = thread(start = true) {
        displayThread()
    }

    fun displayThread() {
        lcd.setDisplayBacklight(true)
        lcd.clearDisplay()
        while (true) {
            try {
                updateDisplay()
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

    private fun sleep() {
        try {
            Thread.sleep(10)
        } catch (e: InterruptedException) {
        }
    }

    companion object {
        private const val MIN_5_MINUTES_BUTTON_PIN = 4
        private const val PLUS_5_MINUTES_BUTTON_PIN = 17
        private const val MOESTUIN_BUTTON_PIN = 27
        private const val GAZON_BUTTON_PIN = 22

        private const val PUMP_ON_LED_PIN = 10
        private const val PUMP_OFF_LED_PIN = 6
        private const val GROENTETUIN_LED_PIN = 11
        private const val GAZON_LED_PIN = 13

        private const val PUMP_SOLENOID_PIN = 19
        private const val AREA_SOLENOID_PIN = 26
    }

}
