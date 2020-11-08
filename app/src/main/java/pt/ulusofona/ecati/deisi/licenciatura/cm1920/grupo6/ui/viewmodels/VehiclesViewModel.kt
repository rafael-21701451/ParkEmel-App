package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.ParkEmelDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.vehicles.VehicleLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckIfLicenseExists
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckVehicles
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnVehicleChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnVehicleGet

class VehiclesViewModel(application: Application): AndroidViewModel(application){

    private val vehicleLogic = VehicleLogic(ParkEmelDatabase.getInstance(application).vehicleDao())

    private var vehicleListener: OnVehicleChange? = null
    private var getVehicleListener: OnVehicleGet? = null
    private var checkIfLicenseExistsListener: CheckIfLicenseExists? = null
    private var checkVehiclesListener: CheckVehicles? = null

    var vehicles: MutableList<Vehicle> = mutableListOf()

    fun registerListener(vehicleListener: OnVehicleChange){
        this.vehicleListener = vehicleListener
        CoroutineScope(Dispatchers.IO).launch{
           vehicleLogic.getVehicles(vehicleListener)
        }
    }

    fun registerListenerLicenseCheck(checkLicenseListener: CheckIfLicenseExists){
        this.checkIfLicenseExistsListener = checkLicenseListener
    }

    fun registerCheckVehiclesListener(checkVehiclesListener: CheckVehicles){
        this.checkVehiclesListener = checkVehiclesListener
    }

    fun registerGetVehicleListener(vehicleGet: OnVehicleGet, uuid: String){
        this.getVehicleListener = vehicleGet
        CoroutineScope(Dispatchers.IO).launch {
            vehicleLogic.getVehicleByIndex(uuid, vehicleGet)
        }
    }

    fun unregisterListener(){
        vehicleListener = null
        getVehicleListener = null
        checkIfLicenseExistsListener = null
        checkVehiclesListener = null
    }

    fun onEditVehicleButton(brand: String, model:String, license:String, date:String, uuid: String){
        CoroutineScope(Dispatchers.IO).launch {
            getVehicleListener?.let { vehicleLogic.editVehicle(brand,model,license,date,uuid, it) }
        }
    }

    fun onAddVehicleButton(brand: String, model: String, license: String, date: String){
        CoroutineScope(Dispatchers.IO).launch {
            checkIfLicenseExistsListener?.let {
                vehicleLogic.insertVehicle(brand, model, license, date,
                    it
                )
            }
        }
    }

    fun onDeleteClick(uuid: String){
        CoroutineScope(Dispatchers.IO).launch {
            vehicleListener?.let { vehicleLogic.removeVehicle(uuid, it) }
        }
    }

    fun goToMessage(message: String){
        vehicleListener?.let { vehicleLogic.goToMessage(message, it) }
    }

    fun checkVehicles(){
        CoroutineScope(Dispatchers.IO).launch {
            checkVehiclesListener?.let { vehicleLogic.checkVehicles(it) }
        }
    }

    fun goToSelectedVehicleMessage(message: String){
        CoroutineScope(Dispatchers.IO).launch {
            checkVehiclesListener?.let { vehicleLogic.goToSelectedVehicleMessage(message,it) }
        }
    }
}