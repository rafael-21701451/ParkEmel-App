package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park

interface OnParkChange {
    fun onParkChange(parkList : MutableList<Park>)

    fun connected()

    fun disconnected()

}