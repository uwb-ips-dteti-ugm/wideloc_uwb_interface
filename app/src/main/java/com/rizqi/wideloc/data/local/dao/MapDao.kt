package com.rizqi.wideloc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rizqi.wideloc.data.local.entity.MapEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) untuk mengelola entitas [MapEntity]
 * yang tersimpan dalam tabel "maps".
 *
 * MapEntity biasanya digunakan untuk menyimpan data peta indoor,
 * seperti layout ruangan, posisi anchor, skala peta, atau metadata lain
 * yang berkaitan dengan sistem pelacakan UWB (WideLoc).
 *
 * DAO ini menyediakan operasi CRUD serta query dasar untuk
 * mengambil seluruh peta atau peta berdasarkan ID.
 */
@Dao
interface MapDao {

    /**
     * Menyimpan satu map ke database.
     * Jika ID sudah ada, data lama akan ditimpa (REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(map: MapEntity)

    /**
     * Menyimpan banyak map sekaligus.
     * Jika ID sudah ada, data lama akan ditimpa.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(maps: List<MapEntity>)

    /**
     * Memperbarui data map yang sudah tersimpan.
     */
    @Update
    suspend fun update(map: MapEntity)

    /**
     * Menghapus satu map dari database.
     */
    @Delete
    suspend fun delete(map: MapEntity)

    /**
     * Menghapus map berdasarkan ID unik.
     *
     * @param id ID unik map.
     */
    @Query("DELETE FROM maps WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Mengambil satu map berdasarkan ID.
     *
     * @return MapEntity atau null jika tidak ditemukan.
     */
    @Query("SELECT * FROM maps WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MapEntity?

    /**
     * Mengambil seluruh map sebagai Flow.
     * Digunakan untuk pemantauan realtime di UI (Live Updates).
     */
    @Query("SELECT * FROM maps")
    fun getAll(): Flow<List<MapEntity>>

    /**
     * Menghapus semua map dalam tabel.
     * Gunakan dengan hati-hati karena tidak dapat dibatalkan.
     */
    @Query("DELETE FROM maps")
    suspend fun deleteAll()
}