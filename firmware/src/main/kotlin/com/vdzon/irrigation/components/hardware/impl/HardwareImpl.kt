package com.vdzon.irrigation.components.hardware.impl

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.*
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalInputProvider
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider
import com.pi4j.util.Console
import com.vdzon.irrigation.components.log.Log
import com.vdzon.irrigation.components.controller.KlepState
import com.vdzon.irrigation.components.hardware.api.EncoderListener
import com.vdzon.irrigation.components.hardware.api.Hardware
import com.vdzon.irrigation.components.hardware.api.KlepListener
import com.vdzon.irrigation.components.hardware.api.SwitchListener
import java.util.*
import kotlin.concurrent.thread


class HardwareImpl(val log: Log) : Hardware {
    /*
    TODO:
     met switchListener de controller aanroepen, die moet dan bepalen wat er gedaan moet worden
     met klepListener ook naar de conroller: die moet de status bijwerken

     Display controller moet iets aparts zijn denk ik

     */

    companion object {
        private const val PIN_BUTTON_1 = 4
        private const val PIN_BUTTON_2 = 17
        private const val PIN_BUTTON_3 = 27
        private const val PIN_BUTTON_4 = 22

        private const val PIN_LED_1 = 10
        private const val PIN_LED_2 = 6//9
        private const val PIN_LED_3 = 11
        private const val PIN_LED_4 = 13//5

        private const val PIN_AAN_UIT = 19
        private const val PIN_RICHTING = 26//6 //12
    }

    private var klepListener: KlepListener? = null
    private var switchListener: SwitchListener? = null
    var displayThread: Thread? = null

    private var encoderListener: EncoderListener? = null

    lateinit var pi4j: Context
    lateinit var displayController: DisplayController

    lateinit var richting: DigitalOutput
    lateinit var aanUit: DigitalOutput
    lateinit var led1: DigitalOutput
    lateinit var led2: DigitalOutput
    lateinit var led3: DigitalOutput
    lateinit var led4: DigitalOutput
    lateinit var switchButton: DigitalInput

    init {
        initHardware()
    }

    override fun klepOpen() {
        aanUit.high()
        klepListener?.klepOpen()
    }

    override fun klepClose() {
        aanUit.low()
        klepListener?.klepClosed()
    }

    override fun updateTime(time: String) {
        displayController.displayData.time = time
    }

    override fun updateIP(ip: String) {
        println("IP adress: $ip")
        displayController.displayData.ip = ip
    }

    override fun updateKlepState(klepState: KlepState) {
        displayController.displayData.klepState = klepState
    }


    override fun encoderUp() {
        encoderListener?.encoderUp()
    }

    override fun encoderDown() {
        encoderListener?.encoderDown()

    }


    override fun registerEncoderListener(encoderListener: EncoderListener) {
        this.encoderListener = encoderListener
    }

    override fun registerSwitchListener(switchListener: SwitchListener) {
        this.switchListener = switchListener
    }

    override fun registerKlepListener(klepListener: KlepListener) {
        this.klepListener = klepListener
    }

    override fun getDisplayData(): DisplayData = displayController.displayData

    override fun start() {
//        val switchState = switchButton.isHigh
//        println(switchState)
//        if (switchButton.isHigh) switchOff() else switchOn()
    }



    fun initHardware() {
//        val piGpio = PiGpio.newNativeInstance()
        pi4j = buildPi4j()

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Releasing GPIO resources...")
            pi4j.shutdown()
        })
        val lcd = LcdDisplay(pi4j, 4, 20, log)
        displayController = DisplayController(lcd)
        displayThread = displayController.startThread()
        printInfo()

        val dout: GpioDDigitalOutputProvider = pi4j.dout()
        val din: GpioDDigitalInputProvider = pi4j.din()

        aanUit = dout.create(PIN_AAN_UIT)
        aanUit.config().shutdownState(DigitalState.LOW)
        aanUit.addListener(System.out::println)
        aanUit.low()

        richting = dout.create(PIN_RICHTING)
        richting.config().shutdownState(DigitalState.LOW)
        richting.addListener(System.out::println)
        richting.low()

        led1 = dout.create(PIN_LED_1)
        led1.config().shutdownState(DigitalState.HIGH)
        led1.addListener(System.out::println)
        led1.high()

        led2 = dout.create(PIN_LED_2)
        led2.config().shutdownState(DigitalState.HIGH)
        led2.addListener(System.out::println)
        led2.high()

        led3 = dout.create(PIN_LED_3)
        led3.config().shutdownState(DigitalState.HIGH)
        led3.addListener(System.out::println)
        led3.high()

        led4 = dout.create(PIN_LED_4)
        led4.config().shutdownState(DigitalState.HIGH)
        led4.addListener(System.out::println)
        led4.high()


        val button3 = createButton("button3", PIN_BUTTON_3, din)
        button3.addListener({ e ->
            if (e.state() === DigitalState.LOW) {
                println("Button3")
                led3.high()
                led2.low()
                richting.high()
            }
        })

        val button4 = createButton("button4", PIN_BUTTON_4, din)
        button4.addListener({ e ->
            if (e.state() === DigitalState.LOW) {
                println("Button4")
                led2.high()
                led3.low()
                richting.low()
            }
        })



        switchButton = createButton("display", PIN_BUTTON_2, din)
        switchButton.addListener({ e ->
            if (e.state() === DigitalState.LOW) {
                println("Button1")
                led1.high()
                led4.low()
                (0..4).forEach { encoderDown() } // 5 down
            }
        })


        val stopButton = createButton("stopbutton", PIN_BUTTON_1, din)
        stopButton.addListener({ e ->
            if (e.state() === DigitalState.LOW) {
                println("Button2")
                led1.low()
                led4.high()
                (0..4).forEach { encoderUp() } // 5 down
            }
        })

        updateTime("")

    }

    private fun createButton(
        id: String,
        pinButton: Int,
        din: GpioDDigitalInputProvider
    ): DigitalInput {
        val properties = Properties()
        properties["id"] = id
        properties["address"] = pinButton
        properties["pull"] = "DOWN"
        properties["name"] = id.uppercase()
        var config = DigitalInput.newConfigBuilder(pi4j)
            .load(properties)
            .build()
        return din.create(config);
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


}

data class DisplayData(
    var manual: Boolean = false,
    var ip: String = "",
    var klepState: KlepState = KlepState.OPEN,
    var time: String = "",
    var plannedTime: String = ""

)

class DisplayController(val lcd: LcdDisplay) {

    val displayData = DisplayData()

    fun startThread() = thread(start = true) {
        displayThread()
    }


    fun displayThread() {
        lcd.setDisplayBacklight(true)
        lcd.clearDisplay()
        updateDisplay()
        while (true) {
            sleep()
            try {
                updateDisplay()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var oldLine1 = ""
    var oldLine2 = ""
    var oldLine3 = ""
    var oldLine4 = ""

    var count = 0L

    private fun updateDisplay() {
        val currentSeconds = System.currentTimeMillis() / 1000
        val isEventSeconds = currentSeconds.mod(2) == 0
        val state = if (isEventSeconds)
            "-"
        else
            "X"


//        println("update display start from ${Thread.currentThread().hashCode()}")

        val line1 = "${displayData.ip}"
        val line2 = "${displayData.klepState.text}"
        val line3 = "$state"
        val line4 = if (displayData.time.isNotEmpty()) {
            "Timer: ${displayData.time}"
        } else if (displayData.plannedTime.isNotEmpty()) {
            "Planned: ${displayData.plannedTime}"
        } else {
            "Geen planning"
        }

        if (oldLine1 != line1) {
            lcd.displayText(line1, 1)
            oldLine1 = line1
        }
        if (oldLine2 != line2) {
            lcd.displayText(line2, 2)
            oldLine2 = line2
        }
        if (oldLine3 != line3) {
            lcd.displayText(line3, 3)
            oldLine3 = line3
        }
        if (oldLine4 != line4) {
            count++
            if (count % 30 == 0L) {
                println("update display $line4")
            }
            lcd.displayText(line4, 4)
            if (count % 30 == 0L) {
                println("updated display $line4")
            }
            oldLine4 = line4
        }

//        println("update display end from ${Thread.currentThread().hashCode()}")
    }

    private fun sleep() {
        try {
            Thread.sleep(10)// met
        } catch (e: InterruptedException) {
        }
    }

}