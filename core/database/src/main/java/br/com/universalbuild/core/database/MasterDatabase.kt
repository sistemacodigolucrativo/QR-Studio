package br.com.universalbuild.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_items")
data class SavedItem(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long
)

@Dao
interface SavedItemDao {
    @Query("SELECT * FROM saved_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SavedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(item: SavedItem)

    @Query("DELETE FROM saved_items WHERE id = :id")
    suspend fun delete(id: String)
}

@Database(entities = [SavedItem::class], version = 1, exportSchema = true)
abstract class MasterDatabase : RoomDatabase() {
    abstract fun savedItemDao(): SavedItemDao
}

/** Add explicit migrations when increasing the schema version. Never destructively migrate. */
object DatabaseMigrations {
    val ALL: Array<Migration> = emptyArray()
}
