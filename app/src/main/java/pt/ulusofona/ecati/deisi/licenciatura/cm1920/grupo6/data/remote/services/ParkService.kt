package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.services

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.responses.ParkResponse
import retrofit2.Response
import retrofit2.http.*

interface ParkService {
    @GET("parking/lots")
    suspend fun getParks(@Header ("api_key") authorization: String): Response<List<ParkResponse>>
}