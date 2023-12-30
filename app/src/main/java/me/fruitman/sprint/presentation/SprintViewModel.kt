package me.fruitman.sprint.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.fruitman.sprint.domain.entities.Stage
import me.fruitman.sprint.domain.use_case.CaseToMoveTaskToColumn

class SprintViewModel : ViewModel() {

    private val caseToMoveTaskToColumn = CaseToMoveTaskToColumn()

    fun onDragToNewColumn(taskId: Int, dropStage: Stage) {
        viewModelScope.launch {
            caseToMoveTaskToColumn.moveTaskToColumn(taskId, dropStage)
        }
    }
}