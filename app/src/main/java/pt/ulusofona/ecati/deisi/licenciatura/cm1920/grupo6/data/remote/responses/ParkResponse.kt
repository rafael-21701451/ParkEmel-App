package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.responses

import com.google.gson.annotations.SerializedName

class ParkResponse{

    @SerializedName("id_parque")
    val idParque = ""

    @SerializedName("nome")
    val nome = ""

    @SerializedName("activo")
    val activo = 0

    @SerializedName("id_entidade")
    val idEntidade = 0

    @SerializedName("capacidade_max")
    val capacidadeMax = 0

    @SerializedName("ocupacao")
    val ocupacao = 0

    @SerializedName("data_ocupacao")
    val dataOcupacao = ""

    @SerializedName("latitude")
    val latitude = ""

    @SerializedName("longitude")
    val longitude = ""

    @SerializedName("tipo")
    val tipo = ""

    override fun toString(): String {
        return "ParkResponse(id_parque='$idParque', nome='$nome', activo=$activo, id_entidade='$idEntidade', capacidade_max='$capacidadeMax', ocupacao='$ocupacao', data_ocupacao='$dataOcupacao', latitude='$latitude', longitude='$longitude', tipo='$tipo')"
    }


}