package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.GiraStation

@Dao
interface GiraDao {

    @Insert
    suspend fun insert(gira: GiraStation)

    @Query("SELECT * FROM girastation")
    suspend fun getAll(): List<GiraStation>

    @Query("DELETE FROM girastation")
    suspend fun deleteAll()

}