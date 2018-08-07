package com.alphavending.testthings

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException


class MainActivity : Activity() {

    private val handler = Handler()
    private lateinit var ledGpio: Gpio
    private var ledState = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button: Button = findViewById(R.id.button)

        button.setOnClickListener {
            Toast.makeText(this@MainActivity, "Hello World", Toast.LENGTH_SHORT).show()
            ledState = !ledState
            ledGpio.value = ledState
        }
        ledGpio = PeripheralManager.getInstance().openGpio("BCM6")
        ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        // Post a Runnable that continuously switch the state of the GPIO, blinking the
        // corresponding LED

    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove pending blink Runnable from the handler.
        // Close the Gpio pin.
        ledGpio.close()
    }
}