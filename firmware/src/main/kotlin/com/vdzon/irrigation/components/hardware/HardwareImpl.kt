package com.vdzon.irrigation.components.hardware

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.*
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider
import com.vdzon.irrigation.api.hardware.Button
import com.vdzon.irrigation.api.hardware.ButtonListener
import com.vdzon.irrigation.api.hardware.Hardware
import com.vdzon.irrigation.api.hardware.Led
import com.vdzon.irrigation.api.log.Log
import com.vdzon.irrigation.api.model.IrrigationArea
import kotlin.concurrent.thread


class HardwareImpl(
    private val log: Log
) : Hardware {
    private lateinit var pi4j: Context
    private lateinit var lcd: LcdDisplay
    private lateinit var areaDigitalOutput: DigitalOutput
    private lateinit var pumpDigitalOutput: DigitalOutput
    private lateinit var pumpOnLedDigitalOutput: DigitalOutput
    private lateinit var pumpOffLedDigitalOutput: DigitalOutput
    private lateinit var moestuinLedDigitalOutput: DigitalOutput
    private lateinit var gazonLedDigitalOutput: DigitalOutput
    private lateinit var moestuin_button: DigitalInput
    private lateinit var gazon_button: DigitalInput
    private lateinit var add_5_button: DigitalInput
    private lateinit var min_5_button: DigitalInput

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
        requiredDisplayLines[lineNr] = line
    }

    override fun registerSwitchListener(switchListener: ButtonListener) {
        this.buttonListener = switchListener
    }

    override fun start() {
        startHardware()
    }

    private fun startHardware() {
        pi4j = buildPi4j()
        Runtime.getRuntime().addShutdownHook(Thread {
            log.logInfo("Releasing GPIO resources...")
            pi4j.shutdown()
        })
        lcd = LcdDisplay(pi4j, 4, 20, log)
        val dout: GpioDDigitalOutputProvider = pi4j.dout()
        val stateChangeLogger = DigitalStateChangeListener { var1 -> log.logInfo(var1.toString()) }

        pumpDigitalOutput = dout.create(PUMP_SOLENOID_PIN)
        pumpDigitalOutput.config().shutdownState(DigitalState.LOW)
        pumpDigitalOutput.addListener(stateChangeLogger)
        pumpDigitalOutput.low()

        areaDigitalOutput = dout.create(AREA_SOLENOID_PIN)
        areaDigitalOutput.config().shutdownState(DigitalState.LOW)
        areaDigitalOutput.addListener(stateChangeLogger)
        areaDigitalOutput.low()

        pumpOnLedDigitalOutput = dout.create(PUMP_ON_LED_PIN)
        pumpOnLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        pumpOnLedDigitalOutput.addListener(stateChangeLogger)
        pumpOnLedDigitalOutput.high()

        pumpOffLedDigitalOutput = dout.create(PUMP_OFF_LED_PIN)
        pumpOffLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        pumpOffLedDigitalOutput.addListener(stateChangeLogger)
        pumpOffLedDigitalOutput.high()

        moestuinLedDigitalOutput = dout.create(GROENTETUIN_LED_PIN)
        moestuinLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        moestuinLedDigitalOutput.addListener(stateChangeLogger)
        moestuinLedDigitalOutput.high()

        gazonLedDigitalOutput = dout.create(GAZON_LED_PIN)
        gazonLedDigitalOutput.config().shutdownState(DigitalState.HIGH)
        gazonLedDigitalOutput.addListener(stateChangeLogger)
        gazonLedDigitalOutput.high()

        moestuin_button = createButton("moestuin_button", MOESTUIN_BUTTON_PIN)
        gazon_button = createButton("gazon_button", GAZON_BUTTON_PIN)
        add_5_button = createButton("add_5_button", PLUS_5_MINUTES_BUTTON_PIN)
        min_5_button = createButton("min_5_button", MIN_5_MINUTES_BUTTON_PIN)

        thread(start = true) {
            startListenButtonThread()
        }

        thread(start = true) {
            startDisplayThread()
        }

    }


    /*
    Gebruik polling om de buttons uit te lezen.
    Gebruik van listeners op de buttons was niet stabiel,
    het gebeurde vaak dan een knop ineens niet meer werkte
     */
    var lastStateMoestuinButton: DigitalState? = null
    var lastStateGazonButton: DigitalState? = null
    var lastStatePlus5Button: DigitalState? = null
    var lastStateMin5Button: DigitalState? = null
    fun startListenButtonThread() {
        lastStateMoestuinButton = moestuin_button.state()
        lastStateGazonButton = gazon_button.state()
        lastStatePlus5Button = add_5_button.state()
        lastStateMin5Button = min_5_button.state()
        while (true) {
            try {
                val newStateMoestuinButton = moestuin_button.state()
                val newStateGazonButton = gazon_button.state()
                val newStatePlus5Button = add_5_button.state()
                val newStateMin5Button = min_5_button.state()

                if (newStateMoestuinButton!=lastStateMoestuinButton) moestuinButtonPressed(newStateMoestuinButton)
                if (newStateGazonButton!=lastStateGazonButton) gazonButtonPressed(newStateGazonButton)
                if (newStatePlus5Button!=lastStatePlus5Button) add5ButtonPressed(newStatePlus5Button)
                if (newStateMin5Button!=lastStateMin5Button) min5ButtonPressed(newStateMin5Button)

                lastStateMoestuinButton = newStateMoestuinButton
                lastStateGazonButton = newStateGazonButton
                lastStatePlus5Button = newStatePlus5Button
                lastStateMin5Button = newStateMin5Button
                sleep()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun moestuinButtonPressed(state: DigitalState) {
        log.logInfo("moestuinButtonPressed:${state}")
        if (state === DigitalState.LOW) buttonListener?.onButtonClick(Button.MOESTUIN_AREA)
    }

    fun gazonButtonPressed(state: DigitalState) {
        log.logInfo("gazonButtonPressed:${state}")
        if (state === DigitalState.LOW) buttonListener?.onButtonClick(Button.GAZON_AREA)
    }

    fun add5ButtonPressed(state: DigitalState) {
        log.logInfo("add5ButtonPressed:${state}")
        if (state === DigitalState.LOW) buttonListener?.onButtonClick(Button.PLUS_5_MINUTES)
    }

    fun min5ButtonPressed(state: DigitalState) {
        log.logInfo("min5ButtonPressed:${state}")
        if (state === DigitalState.LOW) buttonListener?.onButtonClick(Button.MIN_5_MINUTES)
    }


    private fun createButton(
        id: String,
        pinButton: Int
    ): DigitalInput {

        val buttonConfig = DigitalInput.newConfigBuilder(pi4j)
            .id(id)
            .name(id)
            .address(pinButton)
            .pull(PullResistance.PULL_DOWN)
            .debounce(3000L)

        val button = pi4j.create(buttonConfig);
        return button
    }

    private fun buildPi4j(): Context = Pi4J.newAutoContext()

    private fun startDisplayThread() = thread(start = true) {
        displayThread()
    }

    private fun displayThread() {
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
                val line = (requiredLine ?: "").take(20)
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

