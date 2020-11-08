package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.GiraStation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.ParkEmelDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.GiraRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.ParkRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.gira.GiraLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.miscellaneous.MiscellaneousLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.parks.ParkLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnGiraChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange

class TripPlannerViewModel(application: Application): AndroidViewModel(application) {

    private val storage = ParkEmelDatabase.getInstance(application).parkDao()
    private val repository = ParkRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val giraStorage = ParkEmelDatabase.getInstance(application).giraDao()
    private val giraRepository = GiraRepository(giraStorage,RetrofitBuilder.getInstance(ENDPOINT))

    private val parkLogic = ParkLogic(repository)
    private val giraLogic = GiraLogic(giraRepository)
    private val miscellaneousLogic = MiscellaneousLogic()

    private var parkListener: OnParkChange? = null
    private var giraListener: OnGiraChange? = null

    fun registerListener(parkListener: OnParkChange, giraListener: OnGiraChange){
        this.parkListener = parkListener
        this.giraListener = giraListener
        CoroutineScope(Dispatchers.IO).launch{
            parkLogic.getParks(parkListener)
        }
    }

    fun saveParks(parkList: MutableList<Park>){
        parkLogic.savePark(parkList)
    }

    fun getGiras(){
        CoroutineScope(Dispatchers.IO).launch {
            giraListener?.let { giraLogic.getGiras(it) }
        }
    }

    fun getResults(giras: MutableList<GiraStation>): MutableList<Any?>{
        return giraLogic.tripPlannerResult(giras)
    }

    fun unregister(){
        parkListener = null
        giraListener = null
    }

    fun setAddress(latitude: Double, longitude: Double, name: String){
        miscellaneousLogic.setAddress(latitude, longitude, name)
    }

    fun getAddress(): MutableList<Double>{
        return miscellaneousLogic.getAddress()
    }

    fun getAddressName(): String{
        return miscellaneousLogic.getAddressName()
    }
}