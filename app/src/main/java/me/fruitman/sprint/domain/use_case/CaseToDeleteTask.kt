package me.fruitman.sprint.domain.use_case

import me.fruitman.sprint.data.TaskRepo
import me.fruitman.sprint.domain.entities.Task

class CaseToDeleteTask {
    suspend fun deleteTask(task: Task) {
        TaskRepo.deleteTask(task)
    }
}
