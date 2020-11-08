package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    private val storage = ParkEmelDatabase.getInstance(application).parkDao()
    private val repository = ParkRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))

    private val parkLogic = ParkLogic(repository)
    private val miscellaneousLogic = MiscellaneousLogic()

    private var pmnListener: OnParkChange? = null

    fun registerParkMeNowListener(pmnListener: OnParkChange){
        this.pmnListener = pmnListener
        CoroutineScope(Dispatchers.IO).launch {
            parkLogic.getParks(pmnListener)
        }
    }

    fun unregisterListener(){
        pmnListener = null
    }

    fun parkMeNow(parks: MutableList<Park>): Park?{
        return parkLogic.parkMeNow(parks)
    }

    fun getDate(): String{
        return miscellaneousLogic.getDate()
    }
}