package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.services


import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.responses.GiraResponse
import retrofit2.Response
import retrofit2.http.*

interface GiraService {
    @GET("gira/station/list")
    suspend fun getGiras(@Header ("api_key") authorization: String): Response<GiraResponse>
}