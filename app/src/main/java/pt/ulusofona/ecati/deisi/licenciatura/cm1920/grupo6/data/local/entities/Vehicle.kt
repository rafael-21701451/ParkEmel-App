package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Vehicle(var brand: String, var model:String, var licensePlate: String, var date: String ) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
}