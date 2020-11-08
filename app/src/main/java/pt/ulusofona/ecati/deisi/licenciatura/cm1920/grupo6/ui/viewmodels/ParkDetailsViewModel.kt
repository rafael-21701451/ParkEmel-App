package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.ParkEmelDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.ParkRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.parks.ParkLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkGet

class ParkDetailsViewModel(application: Application): AndroidViewModel(application) {

    private val storage = ParkEmelDatabase.getInstance(application).parkDao()
    private val repository = ParkRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))

    private val parkLogic = ParkLogic(repository)

    private var getParkListener: OnParkGet? = null

    fun registerGetParkListener(parkGet: OnParkGet, name: String){
        this.getParkListener = parkGet
        CoroutineScope(Dispatchers.IO).launch {
            parkLogic.getParkByName(name, parkGet)
        }
    }

    fun unregisterListener(){
        getParkListener = null
    }
}