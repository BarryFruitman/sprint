package me.fruitman.sprint.data.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "column") val stageName: String,
    @ColumnInfo(name = "position", defaultValue = "0") val position: Int,
)

@Entity
data class Action(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "taskId") val taskId: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "done") val done: Boolean,
)

data class TaskWithActions(
    @Embedded val task: Task,
    @Relation(parentColumn = "id", entityColumn = "taskId") val actions: List<Action>
)


@Entity
data class TaskPositionUpdate(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "position") val position: Int
)
