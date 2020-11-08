package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.FusedLocation
import java.lang.Exception
import java.math.RoundingMode
import java.util.*

@Entity
class Park(var name: String, var maxCapacity: Int, var currentOccupation: Int, var latitude: Double, var longitude: Double, var type: String, var lastUpdate: String, var active: Int) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    var distance : Double

    init {
        try{
            var location = Location("Park")
            location.latitude = this.latitude
            location.longitude = this.longitude
            distance = (FusedLocation.getLastLocation()?.lastLocation!!.distanceTo(location)/1000).toBigDecimal().setScale(2,
                RoundingMode.UP).toDouble()
        }catch (ex: Exception){
            distance = -1.0
        }
    }

    override fun toString(): String {
        return "Park(name='$name', maxCapacity=$maxCapacity, currentOccupation=$currentOccupation, latitude=$latitude, longitude=$longitude, type='$type', lastUpdate='$lastUpdate', uuid='$uuid', distance=$distance)"
    }


}