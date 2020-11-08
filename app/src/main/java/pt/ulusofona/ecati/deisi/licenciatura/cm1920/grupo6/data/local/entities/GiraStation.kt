package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.FusedLocation
import java.lang.Exception
import java.math.RoundingMode
import java.util.*

@Entity
class GiraStation(var name: String, var bikeNum: Int, var numDocks: Int, var latitude: Double, var longitude: Double) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    var distance: Double

    init {
        try{
            var location = Location("Gira")
            location.latitude = this.latitude
            location.longitude = this.longitude
            distance =
                (FusedLocation.getLastLocation()?.lastLocation!!.distanceTo(location) / 1000).toBigDecimal()
                    .setScale(
                        2,
                        RoundingMode.UP
                    ).toDouble()
        }catch (ex: Exception){
            distance = -1.0
        }

    }

    override fun toString(): String {
        return "GiraStation(name='$name', bikeNum=$bikeNum, freeDocks=$numDocks, latitude=$latitude, longitude=$longitude, uuid='$uuid', distance=$distance)"
    }
}
