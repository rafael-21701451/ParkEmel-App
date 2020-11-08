package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.miscellaneous

import com.google.android.gms.location.LocationResult
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.list.ListStorage
import java.text.SimpleDateFormat
import java.util.*

class MiscellaneousLogic {
    private val storage = ListStorage.getInstance()

    fun setAddress(latitude: Double, longitude: Double, name: String){
        storage.setAddress(latitude, longitude, name)
    }

    fun getAddress(): MutableList<Double>{
        return storage.getAddress()
    }

    fun getAddressName(): String{
        return storage.getAddressName()
    }

    fun getDate(): String{
        val cal = Calendar.getInstance()
        val formatter = SimpleDateFormat("HH")
        return formatter.format(cal.time)
    }

    fun setLastLocation(locationResult: LocationResult?){
        storage.setLastLocation(locationResult)
    }

    fun getLastLocation(): LocationResult? {
        return storage.getLastLocation()
    }
}