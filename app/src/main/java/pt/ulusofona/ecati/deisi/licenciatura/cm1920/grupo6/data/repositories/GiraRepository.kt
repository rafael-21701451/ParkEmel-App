package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.repositories

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.GiraStation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao.GiraDao
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.services.GiraService
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.*
import retrofit2.Retrofit
import java.lang.Exception

class GiraRepository(private val local: GiraDao, private val remote: Retrofit) {

    private val TAG = GiraRepository::class.java.simpleName

    suspend fun loadGiras() {
        val service = remote.create(GiraService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getGiras("93600bb4e7fee17750ae478c22182dda")
                if (response.isSuccessful) {
                    for (g in response.body()!!.features){
                        val gira = GiraStation(
                            g.properties.desigComercial,
                            g.properties.numBicicletas,
                            g.properties.numDocas,
                            g.properties.bbox[0],
                            g.properties.bbox[1]
                        )
                        Log.i(TAG, gira.toString())
                        local.insert(gira)
                    }
                }else{
                    Log.i(TAG, "Unauthorized")
                }
            } catch (ex: Exception) {
                local.getAll()
            }
        }
    }

    suspend fun getGiras(listener: OnGiraChange) {
        val service = remote.create(GiraService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getGiras("93600bb4e7fee17750ae478c22182dda")
                if (response.isSuccessful) {
                    local.deleteAll()
                    var giras = mutableListOf<GiraStation>()
                    for (g in response.body()!!.features){
                        val gira = GiraStation(
                            g.properties.desigComercial,
                            g.properties.numBicicletas,
                            g.properties.numDocas,
                            g.properties.bbox[1],
                            g.properties.bbox[0]
                        )
                        giras.add(gira)
                        local.insert(gira)
                    }
                    listener.onGiraChange(giras)
                }else {
                    Log.i(TAG, "Unauthorized")
                }
            } catch (ex: Exception) {
                listener.onGiraChange(local.getAll() as MutableList<GiraStation>)
            }
        }
    }
}