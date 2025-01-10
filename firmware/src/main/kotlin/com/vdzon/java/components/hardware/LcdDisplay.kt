package com.vdzon.java.components.hardware

import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CConfig
import com.vdzon.java.Log
import kotlin.experimental.inv

/**
 * Implementation of a LCDDisplay using GPIO with Pi4J
 * For now, only works with the PCF8574T Backpack
 */
class LcdDisplay {
    //    private static final int DEFAULT_DEVICE = 0x48;
    /**
     * @return The current running Context
     */
    /**
     * The PI4J I2C component
     */
    val log: Log
    val i2C: I2C

    /**
     * The amount of rows on the display
     */
    private val rows: Int

    /**
     * The amount of columns on the display
     */
    private val columns: Int

    /**
     * With this Byte cursor visibility is controlled
     */
    private var displayControl: Byte = 0

    /**
     * This boolean checks if the backlight is on or not
     */
    private var backlight = false
    /**
     * Creates a new LCDDisplay component with custom rows and columns
     *
     * @param pi4j      Pi4J context
     * @param rows      Custom amount of display lines
     * @param columns   Custom amount of chars on line
     */
    /**
     * Creates a new LCDDisplay component with default values
     *
     * @param pi4j Pi4J context
     */
    @JvmOverloads
    constructor(pi4j: Context, rows: Int = 2, columns: Int = 16, log: Log) {
        this.log = log
        this.rows = rows
        this.columns = columns
        i2C = pi4j.create(buildI2CConfig(pi4j, DEFAULT_BUS, DEFAULT_DEVICE))
        init()
    }

    /**
     * Creates a new LCDDisplay component with custom rows and columns
     *
     * @param pi4j      Pi4J context
     * @param rows      Custom amount of display lines
     * @param columns   Custom amount of chars on line
     * @param bus       Custom I2C bus address
     * @param device    Custom I2C device Address
     */
    constructor(pi4j: Context, rows: Int, columns: Int, bus: Int, device: Int, log: Log) {
        this.log = log
        this.rows = rows
        this.columns = columns
        i2C = pi4j.create(buildI2CConfig(pi4j, bus, device))
        init()
    }

    /**
     * Registers the I2C Component on the PI4J
     *
     * @param pi4j   The Context
     * @param bus    The I2C Bus
     * @param device The I2C Device
     * @return A Provider of an I2C Bus
     */
    private fun buildI2CConfig(pi4j: Context, bus: Int, device: Int): I2CConfig {
        return I2C.newConfigBuilder(pi4j)
            .id("I2C-$device@$bus")
            .name("PCF8574AT")
            .bus(bus)
            .device(device)
            .build()
    }

    /**
     * Turns the backlight on or off
     */
    fun setDisplayBacklight(state: Boolean) {
        backlight = state
        executeCommand(if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT)
    }

    /**
     * Clear the LCD and set cursor to home
     */
    fun clearDisplay() {
        writeCommand(LCD_CLEAR_DISPLAY)
        moveCursorHome()
    }

    /**
     * Returns the Cursor to Home Position (First line, first character)
     */
    fun moveCursorHome() {
        writeCommand(LCD_RETURN_HOME)
        sleep(3, 0)
    }

    /**
     * Shuts the display off
     */
    fun off() {
        executeCommand(LCD_DISPLAY_OFF)
    }

    /**
     * Write a Line of Text on the LCD Display
     *
     * @param text Text to display
     * @param line Select Line of Display
     */
    fun displayText(text: String, line: Int) {
        require(text.length <= columns) { "Too long text. Only $columns characters possible" }
        require(!(line > rows || line < 1)) { "Wrong line id. Only $rows lines possible" }
//        clearLine(line)
        moveCursorHome()
        displayLine(text.padEnd(columns,' '), LCD_ROW_OFFSETS[line - 1].toInt())
    }

    /**
     * Write a Line of Text on the LCD Display
     *
     * @param text     Text to display
     * @param line     Select Line of Display
     * @param position Select position on line
     */
    fun displayText(text: String, line: Int, position: Int) {
        require(position <= columns) { "Too long text. Only $columns characters possible" }
        require(!(line > rows || line < 1)) { "Wrong line id. Only $rows lines possible" }
        clearLine(line)
        setCursorToPosition(position, line)
        for (character in text.toCharArray()) {
            writeCharacter(character)
        }
    }

    /**
     * Write Text on the LCD Display
     *
     * @param text Text to display
     */
    fun displayText(text: String) {
        require(text.length <= rows * columns) { "Too long text. Only " + rows * columns + " characters allowed" }

        // Clean and prepare to write some text
        var currentLine = 0
        val lines: Array<String?> = arrayOfNulls<String>(rows)
        for (j in 0 until rows) {
            lines[j] = ""
        }
        clearDisplay()

        // Iterate through lines and characters and write them to the display
        var i = 0
        while (i < text.length) {

            // line break in text found
            if (text[i] == '\n' && currentLine < rows) {
                currentLine++
                //if a space comes after newLine, it is omitted
                if (i + 1 >= text.length) return
                if (text[i + 1] == ' ') i++
                i++
                continue
            }

            // Write character to array
            lines.set(currentLine,lines.get(currentLine)+ text[i])
            if (lines[currentLine]!!.length == columns && currentLine < rows) {
                currentLine++
                //if a space comes after newLine, it is omitted
                if (i + 1 >= text.length) return
                if (text[i + 1] == ' ') i++
            }
            i++
        }

        //display the created Rows
        for (j in 0 until rows) {
            displayLine(lines[j], LCD_ROW_OFFSETS[j].toInt())
        }
    }

    /**
     * write a character to lcd
     *
     * @param charvalue of the char that is written
     */
    fun writeCharacter(charvalue: Char) {
        writeSplitCommand(charvalue.toByte(), Rs)
    }

    /**
     * write a character to lcd
     *
     * @param charvalue of the char that is written
     * @param line      ROW-position, Range 1 - ROWS
     * @param column    Column-position, Range 0 - COLUMNS-1
     */
    fun writeCharacter(charvalue: Char, column: Int, line: Int) {
        setCursorToPosition(column, line)
        writeSplitCommand(charvalue.toByte(), Rs)
    }

    /**
     * displays a line on a specific position
     *
     * @param text to display
     * @param pos  for the start of the text
     */
    private fun displayLine(text: String?, pos: Int) {
        writeCommand((0x80 + pos).toByte())
        if (text == null || text.isEmpty()) return
        for (i in 0 until text.length) {
            writeCharacter(text[i])
        }
    }

    /**
     * Clears a line of the display
     *
     * @param line Select line to clear
     */
    fun clearLine(line: Int) {
        require(!(line > rows || line < 1)) { "Wrong line id. Only $rows lines possible" }
        displayLine(" ".repeat(columns), LCD_ROW_OFFSETS[line - 1].toInt())
    }

    /**
     * Write a command to the LCD
     */
    private fun writeCommand(cmd: Byte) {
        writeSplitCommand(cmd, 0.toByte())
    }

    /**
     * Write a command in 2 parts to the LCD
     */
    private fun writeSplitCommand(cmd: Byte, mode: Byte) {
        //bitwise AND with 11110000 to remove last 4 bits
        writeFourBits((mode.toInt() or (cmd.toInt() and 0xF0)).toByte())
        //bitshift and bitwise AND to remove first 4 bits
        writeFourBits((mode.toInt() or (cmd.toInt() shl 4 and 0xF0)).toByte())
    }

    /**
     * Write the four bits of a byte to the LCD
     *
     * @param data the byte that is sent
     */
    private fun writeFourBits(data: Byte) {
        i2C.write((data.toInt() or (if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT).toInt()).toByte())
        lcd_strobe(data)
    }

    /**
     * Clocks EN to latch command
     */
    private fun lcd_strobe(data: Byte) {
        i2C.write((data.toInt() or En.toInt() or (if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT).toInt()).toByte())
        sleep(0, 500000)
        i2C.write((data.toInt() and En.inv().toInt() or (if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT).toInt()).toByte())
        sleep(0, 100000)
    }


    /**
     * Sets the cursor to a target destination
     *
     * @param digit Selects the character of the line. Range: 0 - Columns-1
     * @param line  Selects the line of the display. Range: 1 - ROWS
     */
    fun setCursorToPosition(digit: Int, line: Int) {
        require(!(line > rows || line < 1)) { "Line out of range. Display has only " + rows + "x" + columns + " Characters!" }
        require(!(digit < 0 || digit > columns)) { "Line out of range. Display has only " + rows + "x" + columns + " Characters!" }
        writeCommand((LCD_SET_DDRAM_ADDR.toInt() or digit + LCD_ROW_OFFSETS[line - 1]).toByte())
    }

    /**
     * Set the cursor to desired line
     *
     * @param line Sets the cursor to this line. Only Range 1 - lines allowed.
     */
    fun setCursorToLine(line: Int) {
        setCursorToPosition(0, line)
    }

    /**
     * Sets the display cursor to hidden or showing
     *
     * @param show Set the state of the cursor
     */
    fun setCursorVisibility(show: Boolean) {
        if (show) {
            displayControl = (displayControl.toInt() or LCD_CURSOR_ON.toInt()).toByte()
        } else {
            displayControl = (displayControl.toInt() and LCD_CURSOR_ON.inv().toInt()).toByte()
        }
        executeCommand(LCD_DISPLAY_CONTROL, displayControl)
    }

    /**
     * Set the cursor to blinking or static
     *
     * @param blink Blink = true means the cursor will change to blinking mode. False lets the cursor stay static
     */
    fun setCursorBlinking(blink: Boolean) {
        if (blink) {
            displayControl = (displayControl.toInt() or LCD_BLINK_ON.toInt()).toByte()
        } else {
            displayControl = (displayControl.toInt() and LCD_BLINK_ON.inv().toInt()).toByte()
        }
        executeCommand(LCD_DISPLAY_CONTROL, displayControl)
    }

    /**
     * Moves the whole displayed text one character right
     */
    fun moveDisplayRight() {
        executeCommand(LCD_CURSOR_SHIFT, (LCD_DISPLAY_MOVE.toInt() or LCD_MOVE_RIGHT.toInt()).toByte())
    }

    /**
     * Moves the whole displayed text one character right
     */
    fun moveDisplayLeft() {
        executeCommand(LCD_CURSOR_SHIFT, (LCD_DISPLAY_MOVE.toInt() or LCD_MOVE_LEFT.toInt()).toByte())
    }

    /**
     * Create a custom character by providing the single digit states of each pixel. Simply pass an Array of bytes
     * which will be translated to a character.
     *
     * @param location  Set the memory location of the character. 1 - 7 is possible.
     * @param character Byte array representing the pixels of a character
     */
    fun createCharacter(location: Int, character: ByteArray) {
        require(character.size == 8) {
            "Array has invalid length. Character is only 5x8 Digits. Only a array with length" +
                    " 8 is allowed"
        }
        require(!(location > 7 || location < 1)) { "Invalid memory location. Range 1-7 allowed. Value: $location" }
        writeCommand((LCD_SET_CGRAM_ADDR.toInt() or (location shl 3)).toByte())
        for (i in 0..7) {
            writeSplitCommand(character[i], 1.toByte())
        }
    }

    /**
     * Execute Display commands
     *
     * @param command Select the LCD Command
     * @param data    Setup command data
     */
    private fun executeCommand(command: Byte, data: Byte) {
        executeCommand((command.toInt() or data.toInt()).toByte())
    }

    /**
     * Write a single command
     */
    private fun executeCommand(cmd: Byte) {
        i2C.write(cmd)
        sleep(0, 100000)
    }

    /**
     * Initializes the LCD with the backlight off
     */
    private fun init() {
        writeCommand(0x03.toByte())
        writeCommand(0x03.toByte())
        writeCommand(0x03.toByte())
        writeCommand(0x02.toByte())

        // Initialize display settings
        writeCommand((LCD_FUNCTION_SET.toInt() or LCD_2LINE.toInt() or LCD_5x8DOTS.toInt() or LCD_4BIT_MODE.toInt()).toByte())
        writeCommand((LCD_DISPLAY_CONTROL.toInt() or LCD_DISPLAY_ON.toInt() or LCD_CURSOR_OFF.toInt() or LCD_BLINK_OFF.toInt()).toByte())
        writeCommand((LCD_ENTRY_MODE_SET.toInt() or LCD_ENTRY_LEFT.toInt() or LCD_ENTRY_SHIFT_DECREMENT.toInt()).toByte())
        clearDisplay()

        // Enable backlight
        setDisplayBacklight(true)
        log.logDebug("LCD Display initialized")
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds. An [InterruptedException] will be catched and ignored while setting the
     * interrupt flag again.
     */
    protected fun sleep(millis: Long, nanos: Int) {
        try {
            Thread.sleep(millis, nanos)
        } catch (e: InterruptedException) {
//            Thread.currentThread().interrupt()
        }
    }

    companion object {
        /** Flags for display commands  */
        private const val LCD_CLEAR_DISPLAY = 0x01.toByte()
        private const val LCD_RETURN_HOME = 0x02.toByte()
        private const val LCD_ENTRY_MODE_SET = 0x04.toByte()
        private const val LCD_DISPLAY_CONTROL = 0x08.toByte()
        private const val LCD_CURSOR_SHIFT = 0x10.toByte()
        private const val LCD_FUNCTION_SET = 0x20.toByte()
        private const val LCD_SET_CGRAM_ADDR = 0x40.toByte()
        private const val LCD_SET_DDRAM_ADDR = 0x80.toByte()

        // flags for display entry mode
        private const val LCD_ENTRY_RIGHT = 0x00.toByte()
        private const val LCD_ENTRY_LEFT = 0x02.toByte()
        private const val LCD_ENTRY_SHIFT_INCREMENT = 0x01.toByte()
        private const val LCD_ENTRY_SHIFT_DECREMENT = 0x00.toByte()

        // flags for display on/off control
        private const val LCD_DISPLAY_ON = 0x04.toByte()
        private const val LCD_DISPLAY_OFF = 0x00.toByte()
        private const val LCD_CURSOR_ON = 0x02.toByte()
        private const val LCD_CURSOR_OFF = 0x00.toByte()
        private const val LCD_BLINK_ON = 0x01.toByte()
        private const val LCD_BLINK_OFF = 0x00.toByte()

        // flags for display/cursor shift
        private const val LCD_DISPLAY_MOVE = 0x08.toByte()
        private const val LCD_CURSOR_MOVE = 0x00.toByte()
        private const val LCD_MOVE_RIGHT = 0x04.toByte()
        private const val LCD_MOVE_LEFT = 0x00.toByte()

        // flags for function set
        private const val LCD_8BIT_MODE = 0x10.toByte()
        private const val LCD_4BIT_MODE = 0x00.toByte()
        private const val LCD_2LINE = 0x08.toByte()
        private const val LCD_1LINE = 0x00.toByte()
        private const val LCD_5x10DOTS = 0x04.toByte()
        private const val LCD_5x8DOTS = 0x00.toByte()

        // flags for backlight control
        private const val LCD_BACKLIGHT = 0x08.toByte()
        private const val LCD_NO_BACKLIGHT = 0x00.toByte()
        private const val En: Byte = 4.toByte() // Enable bit
        private const val Rw = 2.toByte() // Read/Write bit
        private const val Rs = 1.toByte() // Register select bit

        /**
         * Display row offsets. Offset for up to 4 rows.
         */
        private val LCD_ROW_OFFSETS = byteArrayOf(0x00, 0x40, 0x14, 0x54)

        /**
         * The Default BUS and Device Address.
         * On the PI, you can look it up with the Command 'sudo i2cdetect -y 1'
         */
        private const val DEFAULT_BUS = 0x1
        private const val DEFAULT_DEVICE = 0x27
    }
}
