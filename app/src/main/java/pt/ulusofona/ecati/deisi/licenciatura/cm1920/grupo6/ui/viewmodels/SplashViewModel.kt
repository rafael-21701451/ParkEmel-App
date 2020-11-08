package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.ParkEmelDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.GiraRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories.ParkRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.gira.GiraLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.miscellaneous.MiscellaneousLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.domain.parks.ParkLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GetDataListener

class SplashViewModel (application: Application): AndroidViewModel(application){

    private val storage = ParkEmelDatabase.getInstance(application).parkDao()
    private val repository = ParkRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val giraStorage = ParkEmelDatabase.getInstance(application).giraDao()
    private val giraRepository = GiraRepository(giraStorage,RetrofitBuilder.getInstance(ENDPOINT))

    private val parkLogic = ParkLogic(repository)
    private val giraLogic = GiraLogic(giraRepository)
    private val miscellaneousLogic = MiscellaneousLogic()

    private var splashListener: GetDataListener? = null

    fun registerListener(listener: GetDataListener){
        this.splashListener = listener
        CoroutineScope(Dispatchers.IO).launch {
            parkLogic.checkConnection(listener)
            giraLogic.loadGiras()
        }
    }

    fun unregister(){
        splashListener = null
    }

    fun getDate(): String{
        return miscellaneousLogic.getDate()
    }
}