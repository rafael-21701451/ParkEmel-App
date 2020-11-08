package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.ParkEmelDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.vehicles.VehicleLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckVehicles

class ContactsViewModel(application: Application): AndroidViewModel(application) {

    private val vehicleLogic = VehicleLogic(ParkEmelDatabase.getInstance(application).vehicleDao())

    private var checkVehiclesListener: CheckVehicles? = null

    fun registerCheckVehiclesListener(checkVehiclesListener: CheckVehicles){
        this.checkVehiclesListener = checkVehiclesListener
    }

    fun unregisterListener(){
        checkVehiclesListener = null
    }

    fun checkVehicles(){
        CoroutineScope(Dispatchers.IO).launch {
            checkVehiclesListener?.let { vehicleLogic.checkVehicles(it) }
        }
    }
}