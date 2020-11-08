package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.ParkEmelDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.ParkRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.miscellaneous.MiscellaneousLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.parks.ParkLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange

class MapViewModel (application: Application): AndroidViewModel(application) {

    private val storage = ParkEmelDatabase.getInstance(application).parkDao()
    private val repository = ParkRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))

    private val parkLogic = ParkLogic(repository)
    private val miscellaneousLogic = MiscellaneousLogic()

    private var parkListener: OnParkChange? = null


    fun registerListener(parkListener: OnParkChange){
        this.parkListener = parkListener
        CoroutineScope(Dispatchers.IO).launch {
            parkLogic.getParks(parkListener)
        }
    }

    fun unregisterListener(){
        parkListener = null
    }

    fun getLastLocation(): LocationResult?{
        return miscellaneousLogic.getLastLocation()
    }

    fun updateParkList(){
        CoroutineScope(Dispatchers.IO).launch {
            parkListener?.let { parkLogic.getParks(it) }
        }
    }

    fun applyFilters(parks: MutableList<Park>, typeFilter: String, distanceFilter: Int, occupationFilter: String): MutableList<Park>{
        var filteredList = parkLogic.applyTypeFilter(typeFilter,parks)
        filteredList = parkLogic.applyDistanceFilter(distanceFilter,filteredList)
        filteredList = parkLogic.applyOccupationFilters(occupationFilter,filteredList)
        return filteredList
    }

    fun iconToDisplay(type: String, occupation: Int, active: Int): String{
        return parkLogic.iconToDisplay(type, occupation, active)
    }

    fun setSelectedPark(selectedPark: String){
        parkLogic.setSelectedPark(selectedPark)
    }

    fun getSelectedPark(): String? {
        return parkLogic.getSelectedPark()
    }
}