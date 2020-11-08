package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils

import android.app.Application
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.Accelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.battery.Battery

class ParkEmelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FusedLocation.start(this)
        Battery.start(this)
        Accelerometer.start(this)
    }
}