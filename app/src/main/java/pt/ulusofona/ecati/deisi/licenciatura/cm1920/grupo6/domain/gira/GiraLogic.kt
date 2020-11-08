package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.gira

import android.location.Location
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.GiraStation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.GiraRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnGiraChange
import java.math.RoundingMode

class GiraLogic(private val repository: GiraRepository) {

    private val storage = ListStorage.getInstance()

    suspend fun loadGiras(){
        repository.loadGiras()
    }

    suspend fun getGiras(listener: OnGiraChange){
        repository.getGiras(listener)
    }

    fun tripPlannerResult(giraStations: MutableList<GiraStation>): MutableList<Any?>{
        val addressLatLng = storage.getAddress()
        val address = Location("Address")
        address.latitude = addressLatLng[0]
        address.longitude = addressLatLng[1]

        val parks = storage.getParks()
        var parkResult: Park? = null
        var giraResultStart: GiraStation? = null
        var giraResultEnd: GiraStation? = null
        var longe = false

        for (p in parks){
            val currentOccupation: Int = ((p.currentOccupation.toDouble()/p.maxCapacity.toDouble())*100).toInt()
            if (currentOccupation == 100){
                continue
            }
            if(parkResult == null){
                parkResult = p
            }
            val currentParkLocation = Location("currentPark")
            currentParkLocation.latitude = parkResult.latitude
            currentParkLocation.longitude = parkResult.longitude

            val parkLocation = Location("park")
            parkLocation.latitude = p.latitude
            parkLocation.longitude = p.longitude

            if ((currentParkLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                    RoundingMode.UP).toDouble() > (parkLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                    RoundingMode.UP).toDouble()){
                parkResult = p
            }
        }

        if (parkResult != null){
            for (g in giraStations){
                if (g.numDocks == g.bikeNum){
                    continue
                }
                val currentParkLocation = Location("currentPark")
                currentParkLocation.latitude = parkResult!!.latitude
                currentParkLocation.longitude = parkResult.longitude

                val giraLocation = Location("gira")
                giraLocation.latitude = g.latitude
                giraLocation.longitude = g.longitude

                if ((currentParkLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble() > (giraLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble()){
                    if (giraResultEnd == null){
                        giraResultEnd = g
                    }else{
                        val currentGiraLocation = Location("currentGira")
                        currentGiraLocation.latitude = giraResultEnd.latitude
                        currentGiraLocation.longitude = giraResultEnd.longitude
                        if((giraLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                                RoundingMode.UP).toDouble() < (currentGiraLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                                RoundingMode.UP).toDouble())
                            giraResultEnd = g
                    }
                }
            }

            val finalGiraLocation = Location("giraFinal")
            if(giraResultEnd != null){
                finalGiraLocation.latitude = giraResultEnd!!.latitude
                finalGiraLocation.longitude = giraResultEnd.longitude

                if((finalGiraLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble() > 5.0){
                    longe = true
                }

                for (g in giraStations){
                    if (g.bikeNum == 0){
                        continue
                    }
                    val currentParkLocation = Location("currentPark")
                    currentParkLocation.latitude = parkResult!!.latitude
                    currentParkLocation.longitude = parkResult.longitude

                    val giraLocation = Location("gira")
                    giraLocation.latitude = g.latitude
                    giraLocation.longitude = g.longitude

                    if ((currentParkLocation.distanceTo(giraLocation)/1000).toBigDecimal().setScale(2,
                            RoundingMode.UP).toDouble() <= (currentParkLocation.distanceTo(finalGiraLocation)/2000.0).toBigDecimal().setScale(2,
                            RoundingMode.UP).toDouble()){
                        if (giraResultStart == null){
                            giraResultStart = g
                        }else{
                            val currentFinalGiraLocation = Location("currentFinalGira")
                            currentFinalGiraLocation.latitude = giraResultStart.latitude
                            currentFinalGiraLocation.longitude = giraResultStart.longitude
                            if((giraLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                                    RoundingMode.UP).toDouble() < (currentFinalGiraLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                                    RoundingMode.UP).toDouble())
                                giraResultStart = g
                        }
                    }
                }
            }else {
                val currentParkLocation = Location("TEST")
                currentParkLocation.latitude = parkResult!!.latitude
                currentParkLocation.longitude = parkResult.longitude

                if((currentParkLocation.distanceTo(address)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble() > 5.0){
                    longe = true
                }
            }
        }

        return mutableListOf(longe as Any?,parkResult as Any?,giraResultStart as Any?, giraResultEnd as Any?)
    }
}