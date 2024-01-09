package me.fruitman.sprint.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.fruitman.sprint.data.room.TaskPositionUpdate
import me.fruitman.sprint.domain.entities.Action
import me.fruitman.sprint.domain.entities.Stage
import me.fruitman.sprint.domain.entities.Task

object TaskRepo {
    private val taskDao = me.fruitman.sprint.data.room.database.taskDao()
    private val actionDao = me.fruitman.sprint.data.room.database.actionDao()

    fun getTasksForColumn(stage: Stage): Flow<List<Task>> {
        return taskDao.getTasksForColumn(stage.name).map { tasks ->
            tasks.map { task ->
                task.toEntity()
            }
        }
    }

    fun getTaskById(id: Int): Flow<Task> = taskDao.findById(id).map {
        it.toEntity()
    }.distinctUntilChanged()

    private fun me.fruitman.sprint.data.room.Action.toEntity() =
        Action(
            id = id,
            taskId = taskId,
            description = description,
            done = done,
        )

    private fun Action.toRoom() =
        me.fruitman.sprint.data.room.Action(
            id = id,
            taskId = taskId,
            description = description,
            done = done
        )

    private suspend fun me.fruitman.sprint.data.room.TaskWithActions.toEntity() =
        Task(
            id = task.id,
            title = task.title,
            actions = actions.map { it.toEntity() },
            stage = Stage.fromName(task.stageName)!!,
            position = task.position
        )

    private fun Task.toRoom() =
        me.fruitman.sprint.data.room.Task(
            id = id,
            title = title,
            stageName = stage.name,
            position = position
        )

    suspend fun saveTask(task: Task) {
        val taskId = taskDao.insert(task.toRoom()).toInt()
        actionDao.deleteActionsForTaskId(task.id)
        val actions = task.actions.map { it.copy(taskId = taskId) }
        actions.forEach { action ->
            actionDao.insertAction(action.toRoom())
        }
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toRoom())
    }

    suspend fun updateTasks(tasks: List<Task>) {
        taskDao.updateTasks(
            tasks.mapIndexed { position, task ->
                TaskPositionUpdate(task.id, position)
            })
    }
}