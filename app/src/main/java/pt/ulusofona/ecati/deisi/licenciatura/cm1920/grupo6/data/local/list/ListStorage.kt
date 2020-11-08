package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.list

import com.google.android.gms.location.LocationResult
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park

class ListStorage private constructor() {

    private var uuid: String = ""

    private var parks = mutableListOf<Park>()

    private var addressLatitude = 0.0
    private var addressLongitude = 0.0
    private var addressName = ""

    private var lastLocation: LocationResult? = null

    private var selectedPark: String? = null

    companion object {

        private var instance: ListStorage? = null

        fun getInstance(): ListStorage {
            synchronized(this) {
                if (instance == null) {
                    instance =
                        ListStorage()
                }
                return instance as ListStorage
            }
        }

    }

    fun setAddress(latitude: Double, longitude: Double, name:String){
        this.addressLatitude = latitude
        this.addressLongitude = longitude
        this.addressName = name
    }

    fun getAddress(): MutableList<Double>{
        return mutableListOf<Double>(addressLatitude, addressLongitude)
    }

    fun getAddressName(): String{
        return addressName
    }

    fun setLastUUID(uuid: String){
        this.uuid = uuid
    }

    fun getLastUUID(): String{
        return uuid
    }

    fun setParkList(parks: MutableList<Park>){
        this.parks = parks
    }

    fun getParks() : MutableList<Park>{
        return parks
    }

    fun setLastLocation(locationResult: LocationResult?){
        this.lastLocation = locationResult
    }

    fun getLastLocation(): LocationResult? {
        return lastLocation
    }

    fun setSelectedPark(selectedPark: String){
        this.selectedPark = selectedPark
    }

    fun getSelectedPark(): String? {
        return selectedPark
    }
}