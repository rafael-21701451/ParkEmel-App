package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.vehicles

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao.VehicleDao
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckIfLicenseExists
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckVehicles
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnVehicleChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnVehicleGet

class VehicleLogic(private val local: VehicleDao) {

    suspend fun getVehicles(listener: OnVehicleChange){
        listener.onVehicleChange(local.getAll() as MutableList<Vehicle>)
    }

    suspend fun checkVehicles(listener: CheckVehicles){
        listener.userHasVehicles(local.getAll() as MutableList<Vehicle>)
    }

    suspend fun getVehicleByIndex(uuid: String, listener: OnVehicleGet) {
        listener.onVehicleGet(local.getById(uuid))
    }

    suspend fun editVehicle(brand: String, model:String, license:String, date:String, uuid: String, listener: OnVehicleGet){
        var exists = false
        val vehicles = local.getAll()
        for (v in vehicles){
            if (v.uuid == uuid){
                continue
            }
            if (license == v.licensePlate){
                listener.checkIfLicenseExists(true)
                exists = true
            }
        }
        if (!exists){
            local.updateVehicle(brand, model, license, date, uuid)
            listener.checkIfLicenseExists(false)
        }
    }

    suspend fun insertVehicle(brand: String, model: String, license: String, date: String, listener: CheckIfLicenseExists){
        var exists = false
        val vehicles = local.getAll()
        val vehicle = Vehicle(brand, model, license, date)
        for (v in vehicles){
            if (vehicle.licensePlate == v.licensePlate){
                listener.checkIfLicenseExists(true)
                exists = true
            }
        }
        if (!exists){
            local.insert(vehicle)
            listener.checkIfLicenseExists(false)
        }
    }

    suspend fun removeVehicle(uuid: String, listener: OnVehicleChange){
        local.deleteById(uuid)
        listener.onVehicleChange(local.getAll() as MutableList<Vehicle>)
    }

    fun goToMessage(message: String, listener: OnVehicleChange){
        listener.goToMessage(message)
    }

    fun goToSelectedVehicleMessage(message: String, listener: CheckVehicles){
        listener.goToMessage(message)
    }
}