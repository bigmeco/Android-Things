package com.alphavending.spicer

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.google.android.things.contrib.driver.pwmspeaker.Speaker

import android.os.HandlerThread;
import android.util.Log;


import java.io.IOException;
/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {


    private var mSpeaker: Speaker? = null
    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private val buzzer: Speaker? = null
    private val PLAYBACK_NOTE_DELAY = 80L
    private val TAG = "ttt"
    val DRAMATIC_THEME = doubleArrayOf(391.995, (-1).toDouble(), 391.995, (-1).toDouble(), 391.995, (-1).toDouble(), 311.127, 311.127)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            mSpeaker = Speaker("BCM13")
            mSpeaker!!.stop() // in case the PWM pin was enabled already
        } catch (e: IOException) {
            Log.e(TAG, "Error initializing speaker")
            return  // don't initilize the handler
        }

        mHandlerThread = HandlerThread("pwm-playback")
        mHandlerThread!!.start()
        mHandler = Handler(mHandlerThread!!.getLooper())
        mHandler!!.post(mPlaybackRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mHandler != null) {
            mHandler!!.removeCallbacks(mPlaybackRunnable)
            mHandlerThread!!.quitSafely()
        }
        if (mSpeaker != null) {
            try {
                mSpeaker!!.stop()
                mSpeaker!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing speaker", e)
            } finally {
                mSpeaker = null
            }
        }
    }

    private val mPlaybackRunnable = object : Runnable {

        private var index = 0

        override fun run() {
            if (mSpeaker == null) {
                return
            }

            try {
                if (index == DRAMATIC_THEME.size) {
                    // reached the end
                    mSpeaker!!.stop()
                } else {
                    val note = DRAMATIC_THEME[index++]
                    if (note > 0) {
                        mSpeaker!!.play(note)
                    } else {
                        mSpeaker!!.stop()
                    }
                    mHandler!!.postDelayed(this, PLAYBACK_NOTE_DELAY)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error playing speaker", e)
            }

        }
    }
}