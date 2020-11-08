package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle

interface OnVehicleChange {

    fun onVehicleChange(vehicleList : MutableList<Vehicle>)

    fun goToMessage(message: String)
}