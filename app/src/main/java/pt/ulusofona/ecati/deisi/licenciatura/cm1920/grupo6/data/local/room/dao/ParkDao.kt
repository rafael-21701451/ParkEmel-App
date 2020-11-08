package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park

@Dao
interface ParkDao {

    @Insert
    suspend fun insert(park: Park)

    @Query("SELECT * FROM park")
    suspend fun getAll(): List<Park>

    @Query("SELECT * FROM park LIMIT :pos,1")
    suspend fun getItemInListPos(pos: Int): Park

    @Query("SELECT * FROM park WHERE name = :name")
    suspend fun getByName(name: String): Park

    @Query("DELETE FROM park")
    suspend fun deleteAll()
}