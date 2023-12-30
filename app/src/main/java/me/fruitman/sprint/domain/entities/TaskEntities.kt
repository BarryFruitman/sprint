package me.fruitman.sprint.domain.entities

data class Task(
    val id: Int,
    val title: String,
    val actions: List<Action>,
    val stage: Stage,
    val position: Int
)

enum class Stage(val title: String) {
    Backlog("Backlog"),
    ThisWeek("This Week"),
    Today("Today"),
    WaitingOn("Waiting On"),
    Done("Accomplished");

    companion object {
        fun fromName(name: String?): Stage? = name?.let { values().firstOrNull { it.name == name } }
    }
}

data class Action(
    val id: Int,
    val taskId: Int,
    val description: String,
    val done: Boolean
)
