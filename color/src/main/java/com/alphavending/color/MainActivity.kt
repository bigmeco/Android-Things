package com.alphavending.color

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager


class MainActivity : Activity() {
    private lateinit var ledGpio: Gpio
    private lateinit var ledGpio1: Gpio
    private lateinit var ledGpio2: Gpio
    private var ledState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        ledGpio = PeripheralManager.getInstance().openGpio("BCM19")
        ledGpio2 = PeripheralManager.getInstance().openGpio("BCM5")
        ledGpio1 = PeripheralManager.getInstance().openGpio("BCM13")
        ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        ledGpio1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        ledGpio2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        ledGpio.value = true
        ledGpio1.value = true
        ledGpio2.value = true

    }


    override fun onDestroy() {
        super.onDestroy()
        // Remove pending blink Runnable from the handler.
        // Close the Gpio pin.
        ledGpio.close()
        ledGpio2.close()
        ledGpio1.close()
    }
}
