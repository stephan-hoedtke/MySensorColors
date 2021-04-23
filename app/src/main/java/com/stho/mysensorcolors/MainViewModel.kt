package com.stho.mysensorcolors

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.hardware.SensorManager
import android.view.Surface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.abs


class MainViewModel(application: Application) : AndroidViewModel(application) {

    enum class Mode {
        Proximity,
        RotationVector,
        Gyroscope,
    }
    private val colorLiveData: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = Color.WHITE }
    private val modeLiveData: MutableLiveData<Mode> = MutableLiveData<Mode>().apply { value = Mode.Proximity }
    private val valueLiveData: MutableLiveData<Double> = MutableLiveData<Double>().apply { value = 0.0 }


    internal val colorLD: LiveData<Int>
        get() = colorLiveData

    internal val modeLD: LiveData<Mode>
        get() = modeLiveData

    internal val valueLD: LiveData<Double>
        get() = valueLiveData

    internal var color: Int
        get() = colorLiveData.value ?: Color.WHITE
        set(value) {
            colorLiveData.postValue(value)
        }

    internal var mode: Mode
        get() = modeLiveData.value ?: Mode.Proximity
        set(value) {
            modeLiveData.postValue(value)
        }

    internal var value: Double
        get() = valueLiveData.value ?: 0.0
        set(value) {
            valueLiveData.postValue(value)
        }

    internal var proximityMaxRange: Float = 0f

    internal fun recordProximity(value: Float) {
        if (mode == Mode.Proximity) {
            this.value = value.toDouble()
            when {
                value < 1f -> {
                    // Detected something nearby
                    color = Color.RED
                }
                else -> {
                    // Nothing is nearby
                    color = Color.GREEN
                }
            }
        }
    }

    internal fun recordMagnetometer(values: FloatArray) {
        //
    }

    internal fun recordAcceleration(values: FloatArray) {
        //
    }

    internal fun recordRotationVector(values: FloatArray) {
        if (mode == Mode.RotationVector) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, values)
            val remappedRotationMatrix = getAdjustedRotationMatrix(rotationMatrix)
            val orientations = FloatArray(3)
            SensorManager.getOrientation(remappedRotationMatrix, orientations)
            val azimuth = Math.toDegrees(orientations[0].toDouble())
            val pitch = Math.toDegrees(orientations[1].toDouble())
            val roll = Math.toDegrees(orientations[2].toDouble())
            this.value = pitch
            when {
                azimuth > 20 -> {
                    // anticlockwise
                    color = Color.BLUE
                }
                azimuth < -20 -> {
                    // clockwise
                    color = Color.YELLOW
                }
                pitch > 20 -> {
                    color = Color.RED
                }
                pitch < -20 -> {
                    color = Color.GREEN
                }
                roll > 20 -> {
                    color = Color.DKGRAY
                }
                roll < -20 -> {
                    color = Color.LTGRAY
                }
                else -> {
                    // normal
                    color = Color.WHITE
                }
            }
        }
    }

    internal fun recordGyroscope(values: FloatArray) {
        if (mode == Mode.Gyroscope) {
            this.value = values[0].toDouble()
            when {
                values[2] > 0.4f -> {
                    // anticlockwise
                    color = Color.BLUE
                }
                values[2] < -0.4f -> {
                    // clockwise
                    color = Color.YELLOW
                }
                values[0] > 0.4f -> {
                    color = Color.RED
                }
                values[0] < -0.4f -> {
                    color = Color.GREEN
                }
                values[1] > 0.4f -> {
                    color = Color.DKGRAY
                }
                values[1] < -0.4f -> {
                    color = Color.LTGRAY
                }
                else -> {
                    color = Color.WHITE
                }
            }
        }
    }

    internal var deviceRotation: Int = Surface.ROTATION_0

    /**
     * See the following training materials from google.
     * https://codelabs.developers.google.com/codelabs/advanced-android-training-sensor-orientation/index.html?index=..%2F..advanced-android-training#0
     */
    private fun getAdjustedRotationMatrix(rotationMatrix: FloatArray): FloatArray {
        val rotationMatrixAdjusted = FloatArray(9)
        when (deviceRotation) {
            Surface.ROTATION_90 -> {
                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_Y,
                        SensorManager.AXIS_MINUS_X,
                        rotationMatrixAdjusted
                )
                return rotationMatrixAdjusted
            }
            Surface.ROTATION_180 -> {
                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_MINUS_X,
                        SensorManager.AXIS_MINUS_Y,
                        rotationMatrixAdjusted
                )
                return rotationMatrixAdjusted
            }
            Surface.ROTATION_270 -> {
                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_MINUS_Y,
                        SensorManager.AXIS_X,
                        rotationMatrixAdjusted
                )
                return rotationMatrixAdjusted
            }
            else -> {
                return rotationMatrix
            }
        }
    }
}
