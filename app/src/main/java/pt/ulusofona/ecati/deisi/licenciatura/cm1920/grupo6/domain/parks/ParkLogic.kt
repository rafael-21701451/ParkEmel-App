package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.parks

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.ParkRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GetDataListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GoToPark
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkGet

class ParkLogic(private val repository: ParkRepository) {

    private val storage = ListStorage.getInstance()

    fun setUUID(uuid: String){
        storage.setLastUUID(uuid)
    }

    fun getUUID(): String{
        return storage.getLastUUID()
    }

    suspend fun getParks(listener: OnParkChange){
        repository.getParks(listener)
    }


    fun checkOccupation(occupation: Int): String{
        if(occupation >= 100){
            return "Full"
        }else if (occupation > 90){
            return "Potentially full"
        }else{
            return "Free"
        }
    }

    suspend fun getParkByName (name: String, listener: OnParkGet){
        repository.getPark(name, listener)
    }

    suspend fun getParkByPos(pos: Int, listener: GoToPark){
        repository.getParkInPos(pos, listener)
    }

    fun applyTypeFilter(type: String, parks: MutableList<Park>): MutableList<Park>{
        val filteredParks = mutableListOf<Park>()
        if(type == "Both"){
            return parks
        }else{
            for (p in parks){
                if (p.type == type){
                    filteredParks.add(p)
                }
            }
            return filteredParks
        }

    }

    fun applyDistanceFilter(distance: Int, parks: MutableList<Park>): MutableList<Park>{
        val filteredParks = mutableListOf<Park>()
        if(distance == 0){
            return parks
        }else{
            var distDouble: Double = distance/10.0
            for (p in parks){
                if (p.distance < distDouble){
                    filteredParks.add(p)
                }
            }
            return filteredParks
        }
    }

    fun applyOccupationFilters(occupation: String, parks: MutableList<Park>): MutableList<Park>{
        val filteredParks = mutableListOf<Park>()
        if (occupation == "All"){
            return parks
        } else {
            for (p in parks){
                var currentOccupation: Int = ((p.currentOccupation.toDouble()/ p.maxCapacity.toDouble()) * 100).toInt()
                if (occupation == "Free"){
                    if (currentOccupation < 91){
                        filteredParks.add(p)
                    }
                } else if (occupation == "PFull") {
                    if (currentOccupation in 91..99){
                        filteredParks.add(p)
                    }
                } else if (occupation == "Full") {
                    if (currentOccupation >= 100){
                        filteredParks.add(p)
                    }
                } else if (occupation == "Free_PFull") {
                    if (currentOccupation < 99){
                        filteredParks.add(p)
                    }
                } else if (occupation == "Free_Full") {
                    if (currentOccupation < 91 || currentOccupation >= 100){
                        filteredParks.add(p)
                    }
                } else if (occupation == "PFull_Full") {
                    if (currentOccupation > 90 ){
                        filteredParks.add(p)
                    }
                }
            }
        }
        return filteredParks
    }

    fun iconToDisplay(type: String, occupation: Int, active: Int): String {
        if(active == 0){
            return "inactive"
        }else if (type.substring(0,1) == "S" && occupation < 91){
            return "sup_free"
        }else if (type.substring(0,1) == "S" && occupation  >= 91 && occupation < 100) {
            return "sup_semi"
        }else if (type.substring(0,1) == "S" && occupation >= 100) {
            return "sup_full"
        }else if (type.substring(0,1) == "E" && occupation < 91) {
            return "str_free"
        }else if (type.substring(0,1) == "E" && occupation  >= 91 && occupation < 100) {
            return "str_semi"
        }else {
            return "str_full"
        }
    }

    fun parkMeNow(parks: MutableList<Park>): Park?{
        var park: Park? = null
        for(p in parks){
            var currentOccupation: Int = ((p.currentOccupation.toDouble()/p.maxCapacity.toDouble())*100).toInt()
            if(park == null && currentOccupation < 100){
                park = p
            }
            if (p.distance < park!!.distance){
                park = p
            }
        }
        return park
    }

    fun quickFindResult(parks: MutableList<Park>): MutableList<Park> {
        var quickFindParks = mutableListOf<Park>()
        for (p in parks){
            if (p.distance <= 2){
                quickFindParks.add(p)
            }
        }
        return quickFindParks
    }

    suspend fun checkConnection(listener: GetDataListener){
        repository.checkConnection(listener)
    }

    fun savePark(parks: MutableList<Park>){
        storage.setParkList(parks)
    }

    fun setSelectedPark(selectedPark: String){
        storage.setSelectedPark(selectedPark)
    }

    fun getSelectedPark(): String? {
        return storage.getSelectedPark()
    }
}