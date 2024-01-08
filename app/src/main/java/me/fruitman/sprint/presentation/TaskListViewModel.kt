package me.fruitman.sprint.presentation

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
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
    private val _title = setOf(Stage.Backlog, Stage.Done).find { it == stage }?.title
    val title = MutableLiveData<String?>(_title)
    private val _subtitle = when (stage) {
        Stage.Backlog -> "Everything I Want To Do"
        Stage.Done -> "All I've Accomplished"
        else -> null
    }
    val subtitle = MutableLiveData<String?>(_subtitle)
    private val _showNewTaskButton = setOf(Stage.Backlog, Stage.ThisWeek, Stage.Today).contains(stage)
    val showNewTaskButton = MutableLiveData(_showNewTaskButton)
    val tasks: LiveData<List<TaskItemModel>>
        get() = caseToGetTaskList.getTasksForColumn(stage).map { tasks ->
            tasks.map { task ->
                TaskItemModel(task)
            }
        }.asLiveData(viewModelScope.coroutineContext)

    fun onListOrderChanged(taskList: List<TaskItemModel>) {
        viewModelScope.launch {
            caseToReorderStageTaskList.update(taskList.map { it.task })
        }
    }

    private fun onClick(taskItemModel: TaskItemModel) {
        caseNavigateToEditTask.run(taskItemModel.task)
    }

    inner class TaskItemModel(val task: Task) {
        // TODO: Don't wrap a Task
        fun onClick() {
            this@TaskListViewModel.onClick(this)
        }

        val nextAction: String
            get() = task.actions.firstOrNull()?.description ?: ""
    }
}