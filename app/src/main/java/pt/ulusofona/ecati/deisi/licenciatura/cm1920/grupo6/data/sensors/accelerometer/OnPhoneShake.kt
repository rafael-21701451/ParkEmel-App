package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer

import android.hardware.SensorEvent

interface OnPhoneShake {

    fun onPhoneShake(event: SensorEvent?)
}