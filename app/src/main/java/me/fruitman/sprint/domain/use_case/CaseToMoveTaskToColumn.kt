package me.fruitman.sprint.domain.use_case

import kotlinx.coroutines.flow.first
import me.fruitman.sprint.data.TaskRepo
import me.fruitman.sprint.domain.entities.Stage

class CaseToMoveTaskToColumn {
    suspend fun moveTaskToColumn(taskId: Int, stage: Stage) {
        val task = TaskRepo.getTaskById(taskId).first()
        val movedTask = task.copy(stage = stage)
        TaskRepo.saveTask(movedTask)
    }
}