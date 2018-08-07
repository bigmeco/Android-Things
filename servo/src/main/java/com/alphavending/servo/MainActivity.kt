package com.alphavending.servo

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.Pwm
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : Activity() {


    private val handler = Handler()
    private lateinit var pwm: Pwm
    private var isPulseIncreasing = true
    private var activePulseDuration: Double = 0.0

    private val changePWMRunnable = object : Runnable {
        override fun run() {
            // Change the duration of the active PWM pulse, but keep it between the minimum and
            // maximum limits.
            // The direction of the change depends on the isPulseIncreasing variable, so the pulse
            // will bounce from MIN to MAX.
            if (isPulseIncreasing) {
                activePulseDuration += PULSE_CHANGE_PER_STEP_MS
            } else {
                activePulseDuration -= PULSE_CHANGE_PER_STEP_MS
            }

            // Bounce activePulseDuration back from the limits
            if (activePulseDuration > MAX_ACTIVE_PULSE_DURATION_MS) {
                activePulseDuration = MAX_ACTIVE_PULSE_DURATION_MS
                isPulseIncreasing = !isPulseIncreasing
            } else if (activePulseDuration < MIN_ACTIVE_PULSE_DURATION_MS) {
                activePulseDuration = MIN_ACTIVE_PULSE_DURATION_MS
                isPulseIncreasing = !isPulseIncreasing
            }

            Log.d(TAG, "Changing PWM active pulse duration to ${activePulseDuration} ms")

            try {

                // Duty cycle is the percentage of active (on) pulse over the total duration of the
                // PWM pulse
                pwm.setPwmDutyCycle(100 * activePulseDuration / PULSE_PERIOD_MS)

                // Reschedule the same runnable in {@link #INTERVAL_BETWEEN_STEPS_MS} milliseconds
                handler.postDelayed(this, INTERVAL_BETWEEN_STEPS_MS)
            } catch (e: IOException) {
                Log.e(TAG, "Error on PeripheralIO API", e)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "Starting PwmActivity")

        activePulseDuration = MIN_ACTIVE_PULSE_DURATION_MS

        pwm = PeripheralManager.getInstance().openPwm("PWM0")
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d(TAG, progress.toString())
                INTERVAL_BETWEEN_STEPS_MS=progress.toLong()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        // Always set frequency and initial duty cycle before enabling PWM
        pwm.setPwmFrequencyHz(1000 / PULSE_PERIOD_MS)
        pwm.setPwmDutyCycle(activePulseDuration)
        pwm.setEnabled(true)

        // Post a Runnable that continuously change PWM pulse width, effectively changing the
        // servo position
        Log.d(TAG, "Start changing PWM pulse")
        handler.post(changePWMRunnable)

    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove pending Runnable from the handler.
        handler.removeCallbacks(changePWMRunnable)
        // Close the PWM port.
        Log.i(TAG, "Closing port")
        pwm.close()
    }

    companion object {
        private val TAG = "tags"

        // Parameters of the servo PWM
        const private val MIN_ACTIVE_PULSE_DURATION_MS = 1.0
        const private val MAX_ACTIVE_PULSE_DURATION_MS = 2.0
        const private val PULSE_PERIOD_MS = 20.0  // Frequency of 50Hz (1000/20)

        // Parameters for the servo movement over time
         private var PULSE_CHANGE_PER_STEP_MS = 10
         private var INTERVAL_BETWEEN_STEPS_MS: Long = 200
    }
}
