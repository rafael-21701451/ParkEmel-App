package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.ParkEmelDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.ParkRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.parks.ParkLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GoToPark
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkGet

const val ENDPOINT = "https://emel.city-platform.com/opendata/"

class ParksViewModel(application: Application): AndroidViewModel(application) {

    private val storage = ParkEmelDatabase.getInstance(application).parkDao()
    private val repository = ParkRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))

    private val parkLogic = ParkLogic(repository)

    private var parkListener: OnParkChange? = null
    private var getParkListener: OnParkGet? = null
    private var goToParkListener: GoToPark? = null

    var parks: MutableList<Park> = mutableListOf()

    fun registerListener(parkListener: OnParkChange,goToPark: GoToPark){
        this.parkListener = parkListener
        this.goToParkListener = goToPark
        CoroutineScope(Dispatchers.IO).launch {
            parkLogic.getParks(parkListener)
        }
    }

    fun unregisterListener(){
        parkListener = null
        getParkListener = null
        goToParkListener = null
    }

    fun checkOccupation(occupation: Int): String{
        return parkLogic.checkOccupation(occupation)
    }

    fun getParkInPos(pos:Int){
        CoroutineScope(Dispatchers.IO).launch {
            goToParkListener?.let { parkLogic.getParkByPos(pos, it) }
        }
    }

    fun applyFilters(parks: MutableList<Park>, typeFilter: String, distanceFilter: Int, occupationFilter: String): MutableList<Park>{
        var filteredList = parkLogic.applyTypeFilter(typeFilter,parks)
        filteredList = parkLogic.applyDistanceFilter(distanceFilter,filteredList)
        filteredList = parkLogic.applyOccupationFilters(occupationFilter,filteredList)
        return filteredList
    }

    fun updateParkList(){
        CoroutineScope(Dispatchers.IO).launch {
            parkListener?.let { parkLogic.getParks(it) }
        }
    }

}