package me.fruitman.sprint.domain.use_case

import me.fruitman.sprint.data.TaskRepo
import me.fruitman.sprint.domain.entities.Task

class CaseToReorderStageTaskList {
    suspend fun update(tasks: List<Task>) {
        val orderedTasks = tasks.mapIndexed { position, task ->
            task.copy(position = position)
        }

        TaskRepo.updateTasks(orderedTasks)
    }
}