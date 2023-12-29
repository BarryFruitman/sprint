package me.fruitman.sprint.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun getAllTasks(): Flow<List<TaskWithActions>>

    @Query("SELECT * FROM Task WHERE column = :columnName ORDER BY position")
    fun getTasksForColumn(columnName: String): Flow<List<TaskWithActions>>

    @Query("SELECT * FROM Task WHERE id = :id LIMIT 1")
    fun findById(id: Int): Flow<TaskWithActions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM Task WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Update(entity = Task::class)
    suspend fun updateTasks(tasks: List<TaskPositionUpdate>)
}
