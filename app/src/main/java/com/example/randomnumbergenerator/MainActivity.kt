package com.example.randomnumbergenerator

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.randomnumbergenerator.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private var nrOfNumbers: Int = -1
    private var minVal: Int = -1
    private var maxVal: Int = -1
    private val nrStrings = arrayListOf<TextView>()
    private var generatedNr = arrayListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val acqData = arrayListOf<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        nrStrings.add(binding.textNr0)
        nrStrings.add(binding.textNr1)
        nrStrings.add(binding.textNr2)
        nrStrings.add(binding.textNr3)
        nrStrings.add(binding.textNr4)
        nrStrings.add(binding.textNr5)
        nrStrings.add(binding.textNr6)
        nrStrings.add(binding.textNr7)
        nrStrings.add(binding.textNr8)
        nrStrings.add(binding.textNr9)

        binding.generateButton.setOnClickListener { generateNumbers() }
        binding.showNumbersButton.setOnClickListener { showNumbers() }
        binding.shakeAgainButton.setOnClickListener { shakeAgain() }
        binding.resetButton.setOnClickListener { reset() }
    }

    private fun generateNumbers() {
        val nrOfNumbersString = binding.nrOfNumbersText.text.toString()
        val minValueString = binding.minimumValueText.text.toString()
        val maxValueString = binding.maximumValueText.text.toString()

        if (nrOfNumbersString == "" || minValueString == "" || maxValueString == "") {
            Toast.makeText(this, "Please insert all values", Toast.LENGTH_SHORT).show()
        } else {
            nrOfNumbers = nrOfNumbersString.toInt()
            minVal = minValueString.toInt()
            maxVal = maxValueString.toInt()

            if (nrOfNumbers > 10) {
                nrOfNumbers = 10
            }

            if (nrOfNumbers < 1) {
                nrOfNumbers = 1
            }

            if (minVal < 0) {
                minVal = 0
            }

            if (minVal > 100) {
                minVal = 100
            }

            if (maxVal < 0) {
                maxVal = 0
            }

            if (maxVal > 100) {
                maxVal = 100
            }

            if (minVal > maxVal) {
                minVal = maxVal
            }

            binding.apply {
                generateButton.visibility = View.GONE
                nrOfNumbersText.visibility = View.GONE
                minimumValueText.visibility = View.GONE
                maximumValueText.visibility = View.GONE
                shakeNowText.visibility = View.VISIBLE
                showNumbersButton.visibility = View.VISIBLE
            }

            setUpSensorStuff()
        }
    }

    private fun showNumbers() {
        parseNumbers()
        unregisterSensor()

        binding.apply {
            shakeNowText.visibility = View.GONE
            showNumbersButton.visibility = View.GONE
            resetButton.visibility = View.VISIBLE
            shakeAgainButton.visibility = View.VISIBLE
        }

        for (i in 0 until nrOfNumbers) {
            nrStrings[i].visibility = View.VISIBLE
        }
    }

    private fun shakeAgain() {
        setUpSensorStuff()

        binding.apply {
            shakeNowText.visibility = View.VISIBLE
            showNumbersButton.visibility = View.VISIBLE
            resetButton.visibility = View.GONE
            shakeAgainButton.visibility = View.GONE
        }

        for (i in 0 until nrOfNumbers) {
            nrStrings[i].visibility = View.GONE
        }
    }

    private fun reset() {
        unregisterSensor()

        for (i in 0 until nrOfNumbers) {
            nrStrings[i].visibility = View.GONE
        }

        nrOfNumbers = -1
        minVal = -1
        maxVal = -1

        binding.apply {
            generateButton.visibility = View.VISIBLE
            nrOfNumbersText.visibility = View.VISIBLE
            minimumValueText.visibility = View.VISIBLE
            maximumValueText.visibility = View.VISIBLE
            resetButton.visibility = View.GONE
            shakeAgainButton.visibility = View.GONE
        }
    }

    private lateinit var sensorManager: SensorManager

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun unregisterSensor() {
        generatedNr = arrayListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        acqData.clear()
        sensorManager.unregisterListener(this)
    }

    private fun parseNumbers() {
        while (acqData.size < 100) {
            acqData.add(0f)
        }

        for (i in 0..9) {
            for (j in 0..9) {
                generatedNr[j] += acqData[10 * i + j]
            }
        }

        for (i in 0..9) {
            generatedNr[i] = generatedNr[i] * 31
        }

        for (i in 0..9) {
            generatedNr[i] = generatedNr[i].toInt().toFloat() % (maxVal - minVal + 1)
            generatedNr[i] = generatedNr[i] + minVal
        }

        for (i in 0..9) {
            nrStrings[i].text = generatedNr[i].toInt().toString()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            if (acqData.size > 100) {
                acqData.removeAt(0)
                acqData.removeAt(0)
                acqData.removeAt(0)
            }

            acqData.add(abs(event.values[0]))
            acqData.add(abs(event.values[1]))
            acqData.add(abs(event.values[2]))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
}
