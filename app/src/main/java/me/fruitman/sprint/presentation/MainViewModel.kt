package me.fruitman.sprint.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.fruitman.sprint.domain.entities.Stage
import me.fruitman.sprint.domain.use_case.CaseToMoveTaskToColumn

class MainViewModel : ViewModel() {

    private val caseToMoveTaskToColumn = CaseToMoveTaskToColumn()


    fun onDragToNewColumn(taskId: Int, stage: Stage) {
        viewModelScope.launch {
            caseToMoveTaskToColumn.moveTaskToColumn(taskId, stage)
        }
    }

}
