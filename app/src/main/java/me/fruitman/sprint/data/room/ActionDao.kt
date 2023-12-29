package me.fruitman.sprint.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {
    @Query("SELECT * FROM Action WHERE id = :id LIMIT 1")
    fun getActionForId(id: Int): Flow<Action>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: Action): Long

    @Query("SELECT * FROM Action WHERE taskId = :taskId")
    fun getActionsForTask(taskId: Int): Flow<List<Action>>

    @Query("DELETE FROM Action WHERE taskId = :taskId")
    suspend fun deleteActionsForTaskId(taskId: Int)
}
