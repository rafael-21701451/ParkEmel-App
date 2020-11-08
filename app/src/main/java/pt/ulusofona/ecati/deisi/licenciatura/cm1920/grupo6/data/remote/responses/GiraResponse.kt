package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.remote.responses

import com.google.gson.annotations.SerializedName

class GiraResponse{

    @SerializedName("type")
    val type = ""

    @SerializedName("totalFeatures")
    val totalFeatures = 0

    @SerializedName("features")
    val features = mutableListOf<Features>()

   class Features{

       @SerializedName("type")
       val type = ""

       @SerializedName("geometry")
       val geometry = Geometry()

       class Geometry{
           @SerializedName("type")
           val type = ""

           @SerializedName("coordinates")
           val coordinates = mutableListOf<MutableList<Double>>()

           override fun toString(): String {
               return "Geometry(type='$type', coordinates=$coordinates)"
           }


       }

       @SerializedName("properties")
        var properties = Properties()

       class Properties{
           @SerializedName("id_expl")
           var idExpl = ""

           @SerializedName("id_planeamento")
           var idPlaneamento = ""

           @SerializedName("desig_comercial")
           var desigComercial = ""

           @SerializedName("tipo_servico_niveis")
           var tipoServicoNiveis = ""

           @SerializedName("num_bicicletas")
           var numBicicletas = 0

           @SerializedName("num_docas")
           var numDocas = 40

           @SerializedName("racio")
           var racio = 0.0

           @SerializedName("estado")
           var estado = ""

           @SerializedName("update_date")
           var updateDate = ""

           @SerializedName("bbox")
           var bbox = mutableListOf<Double>()

           override fun toString(): String {
               return "Properties(idExpl='$idExpl', idPlaneamento='$idPlaneamento', desigComercial='$desigComercial', tipoServicoNiveis='$tipoServicoNiveis', numBicicletas=$numBicicletas, numDocas=$numDocas, racio=$racio, estado='$estado', updateDate='$updateDate', bbox=$bbox)"
           }


       }

       override fun toString(): String {
           return "Features(type='$type', geometry=$geometry, properties=$properties)"
       }


   }

    override fun toString(): String {
        return "GiraResponse(type='$type', totalFeatures=$totalFeatures, features=$features)"
    }

}

