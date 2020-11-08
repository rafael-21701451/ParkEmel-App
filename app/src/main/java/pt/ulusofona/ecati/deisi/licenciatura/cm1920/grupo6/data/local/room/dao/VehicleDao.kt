package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao

import androidx.room.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle

@Dao
interface VehicleDao {

    @Insert
    suspend fun insert(vehicle: Vehicle)

    @Query("SELECT * FROM vehicle")
    suspend fun getAll(): List<Vehicle>

    @Query("SELECT * FROM vehicle WHERE uuid = :uuid")
    suspend fun getById(uuid: String): Vehicle

    @Query("UPDATE vehicle SET brand = :brand, model = :model, licensePlate = :licensePlate, date = :date WHERE uuid = :uuid")
    suspend fun updateVehicle(brand: String, model: String, licensePlate: String, date: String, uuid: String)

    @Query("DELETE FROM vehicle WHERE uuid = :uuid")
    suspend fun deleteById(uuid: String)
}