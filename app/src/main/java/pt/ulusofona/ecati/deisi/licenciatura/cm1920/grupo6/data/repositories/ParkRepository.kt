package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories

import android.location.Location
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao.ParkDao
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.services.ParkService
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GetDataListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GoToPark
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkGet
import retrofit2.Retrofit
import java.lang.Exception
import java.math.RoundingMode

class ParkRepository(private val local: ParkDao, private val remote: Retrofit) {

    private val TAG = ParkRepository::class.java.simpleName

    suspend fun getParks(listener: OnParkChange) {
        val service = remote.create(ParkService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getParks("93600bb4e7fee17750ae478c22182dda")
                if (response.isSuccessful) {
                    local.deleteAll()
                    val parks = mutableListOf<Park>()
                    for (park in response.body()!!) {
                        val p = Park(
                            park.nome,
                            park.capacidadeMax,
                            park.ocupacao,
                            park.latitude.toDouble(),
                            park.longitude.toDouble(),
                            park.tipo,
                            park.dataOcupacao,
                            park.activo
                        )
                        parks.add(p)
                        Log.i(TAG, park.toString())
                    }
                    parks.sortBy { park: Park -> park.distance }
                    for (p in parks) {
                        local.insert(p)
                    }
                    listener.connected()
                    listener.onParkChange(parks)
                } else {
                    Log.i(TAG, "Unauthorized")
                }
            } catch (ex: Exception) {
                listener.disconnected()
                val parks = mutableListOf<Park>()
                for (park in local.getAll()){
                    parks.add(park)
                }
                for (p in parks){
                    var location = Location("Park")
                    location.latitude = p.latitude
                    location.longitude = p.longitude
                    try {
                        p.distance = (FusedLocation.getLastLocation()?.lastLocation!!.distanceTo(location)/1000).toBigDecimal().setScale(2,
                            RoundingMode.UP).toDouble()
                    }catch (ex: Exception){
                        p.distance = -1.0
                    }
                }
                parks.sortBy { p: Park -> p.distance }
                listener.onParkChange(parks)
            }
        }
    }

    suspend fun checkConnection(listener: GetDataListener) {
        val service = remote.create(ParkService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getParks("93600bb4e7fee17750ae478c22182dda")
                if (response.isSuccessful) {
                    listener.connected()
                } else {
                    Log.i(TAG, "Unauthorized")
                }
            } catch (ex: Exception) {
                listener.disconnected()
            }
        }
    }

    suspend fun getPark(name: String, listener: OnParkGet) {
        listener.onParkGet(local.getByName(name))
    }

    suspend fun getParkInPos(pos: Int, listener: GoToPark) {
        listener.goToPark(local.getItemInListPos(pos))
    }
}