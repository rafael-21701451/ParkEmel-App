package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.GiraStation

interface OnGiraChange {
    fun onGiraChange(giraList : MutableList<GiraStation>)
}