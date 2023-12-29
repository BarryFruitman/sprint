package me.fruitman.sprint.presentation

import android.app.Application

class SprintApplication : Application() {

    companion object {
        lateinit var instance: SprintApplication
    }

    init {
        instance = this
    }
}