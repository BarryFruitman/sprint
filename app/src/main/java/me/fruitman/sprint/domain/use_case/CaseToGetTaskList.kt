package me.fruitman.sprint.domain.use_case

import kotlinx.coroutines.flow.Flow
import me.fruitman.sprint.data.TaskRepo
import me.fruitman.sprint.domain.entities.Stage
import me.fruitman.sprint.domain.entities.Task

class CaseToGetTaskList {
    fun getTasksForColumn(stage: Stage): Flow<List<Task>> {
        return TaskRepo.getTasksForColumn(stage)
    }
}