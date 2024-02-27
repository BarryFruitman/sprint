package me.fruitman.sprint.presentation

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.fruitman.sprint.domain.entities.Action
import me.fruitman.sprint.domain.entities.Stage
import me.fruitman.sprint.domain.entities.Task
import me.fruitman.sprint.domain.use_case.CaseToDeleteTask
import me.fruitman.sprint.domain.use_case.CaseToSaveTask
import me.fruitman.sprint.domain.use_case.CaseToViewTask

class EditTaskViewModel(private val arguments: Bundle) : ViewModel() {

    private var nextId = 0
    data class ActionItem(val id: Int, val description: String, val done: Boolean)

    val open = MutableLiveData(true)
    val title = MutableLiveData<String>()
    val selectedStage = MutableLiveData(Stage.ThisWeek.name)
    val actions = MutableLiveData<List<ActionItem>>()

    private val caseToViewTask = CaseToViewTask()
    private val caseToSaveTask = CaseToSaveTask()
    private val caseToDeleteTask = CaseToDeleteTask()
    private lateinit var task: Task

    fun onClickSave() {
        viewModelScope.launch {
            caseToSaveTask.saveTask(
                Task(
                    id = task.id,
                    title = title.value!!,
                    actions = actions.value?.toActions() ?: emptyList(),
                    stage = Stage.fromName(selectedStage.value)!!,
                    position = task.position
                )
            )

            open.value = false
        }
    }

    fun onClickDelete() {
        viewModelScope.launch {
            caseToDeleteTask.deleteTask(task)
        }
        open.value = false
    }

    fun onViewReady() = loadTask()

    private fun loadTask() {
        viewModelScope.launch {
            bindTask(fetchTask())
        }
    }

    private suspend fun fetchTask(): Task {
        val taskId = arguments.getInt("taskId")
        return  caseToViewTask.getTaskById(taskId)?.first() ?: Task( // New empty task
            id = taskId,
            title = "",
            actions = emptyList(),
            stage = Stage.Backlog,
            position = 0
        )
    }

    private fun Action.toViewEntity() = ActionItem(nextId++, description, done)

    private fun List<ActionItem>.toActions() = filter { it.description.isNotBlank() }.map { it.toAction() }

    private fun ActionItem.toAction() = Action(0, task.id, description, done)

    private fun bindTask(task: Task) {
        this.task = task

        Log.d("EDIT_DEBUG", "title: ${task.title}")

        title.value = task.title
        actions.value = task.actions.map { it.toViewEntity() }
        selectedStage.value = task.stage.name
    }

    fun appendNewAction() {
        actions.value = (actions.value ?: emptyList()) + ActionItem(nextId++, "", false)
    }

    fun onActionChanged(newAction: ActionItem) {
        val newActions = this.actions.value?.toMutableList() ?: return
        val iAction = newActions.indexOfFirst { it.id == newAction.id }

        newActions[iAction] = newAction

        if (!this.actions.value!![iAction].done && newAction.done) {
            markActionDone(newActions, newAction)
        }

        actions.value = newActions
    }

    // Move it to the bottom
    private fun markActionDone(actionsIn: MutableList<ActionItem>, doneAction: ActionItem) {
        val appendId = actionsIn[actionsIn.size - 1].id
        val newActionPos = actionsIn.indexOfFirst { it.id == doneAction.id }

        actionsIn.removeAt(newActionPos)
        val appendPos = actionsIn.indexOfFirst { it.id == appendId }
        actionsIn.add(appendPos + 1, doneAction)
    }

    fun onStageChanged(stageName: String) {
        this.selectedStage.value = stageName
    }
}