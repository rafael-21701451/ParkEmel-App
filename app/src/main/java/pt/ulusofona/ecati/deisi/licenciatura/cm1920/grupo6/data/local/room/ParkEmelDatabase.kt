package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.GiraStation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao.GiraDao
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao.ParkDao
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao.VehicleDao


@Database(entities = [Park::class, Vehicle::class, GiraStation::class], version = 1)
abstract class ParkEmelDatabase: RoomDatabase(){

    abstract fun parkDao(): ParkDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun giraDao(): GiraDao

    companion object{
        private var instance: ParkEmelDatabase? = null

        fun getInstance(applicationContext: Context): ParkEmelDatabase{
            synchronized(this){
                if (instance == null){
                    instance = Room.databaseBuilder(applicationContext,ParkEmelDatabase::class.java,"parkemel_db").build()
                }
                return instance as ParkEmelDatabase
            }
        }
    }
}