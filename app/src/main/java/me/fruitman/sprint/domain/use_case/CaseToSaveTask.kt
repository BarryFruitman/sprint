package me.fruitman.sprint.domain.use_case

import me.fruitman.sprint.data.TaskRepo
import me.fruitman.sprint.domain.entities.Task

class CaseToSaveTask {
    suspend fun saveTask(task: Task) {
        TaskRepo.saveTask(task)
    }
}
