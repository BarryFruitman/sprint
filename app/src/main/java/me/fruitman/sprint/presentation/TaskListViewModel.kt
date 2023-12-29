package me.fruitman.sprint.presentation

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.fruitman.sprint.domain.entities.Stage
import me.fruitman.sprint.domain.entities.Task
import me.fruitman.sprint.domain.use_case.CaseNavigateToEditTask
import me.fruitman.sprint.domain.use_case.CaseToGetTaskList
import me.fruitman.sprint.domain.use_case.CaseToReorderStageTaskList

class TaskListViewModel(arguments: Bundle) : ViewModel() {

    private val caseToGetTaskList = CaseToGetTaskList()
    private val caseNavigateToEditTask = CaseNavigateToEditTask()
    private val caseToReorderStageTaskList = CaseToReorderStageTaskList()
    private val stage: Stage = Stage.fromName(arguments.getString("stage_name")) ?: Stage.ThisWeek // ERROR
    val tasks = MutableLiveData<List<TaskItemModel>>(emptyList())

    init {
        viewModelScope.launch {
            caseToGetTaskList.getTasksForColumn(stage).collect { tasks ->
                this@TaskListViewModel.tasks.value = tasks.map { task ->
                    TaskItemModel(task)
                }
            }
        }
    }

    fun onListOrderChanged(taskList: List<TaskItemModel>) {
        viewModelScope.launch {
            caseToReorderStageTaskList.update(taskList.map { it.task })
        }
    }

    private fun onClick(taskItemModel: TaskItemModel) {
        caseNavigateToEditTask.run(taskItemModel.task)
    }

    inner class TaskItemModel(val task: Task) {
        fun onClick() {
            this@TaskListViewModel.onClick(this)
        }

        val nextAction: String
            get() = task.actions.firstOrNull()?.description ?: ""
    }
}