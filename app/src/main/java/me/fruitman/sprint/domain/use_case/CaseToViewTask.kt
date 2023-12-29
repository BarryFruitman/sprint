package me.fruitman.sprint.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.fruitman.sprint.data.TaskRepo
import me.fruitman.sprint.domain.entities.Action
import me.fruitman.sprint.domain.entities.Task

class CaseToViewTask {

//    init {
//        TaskRepo.getAllTasks().map { tasks ->
//            tasks.forEach { task ->
//                TaskRepo.insertActions(Action(0, task.actions[0].description, false))
//            }
//        }
//    }

    fun getTaskById(taskId: Int?): Flow<Task>? =
        if (taskId != null && taskId > 0) {
            taskId.let { TaskRepo.getTaskById(taskId) }
        } else null
}