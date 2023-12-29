package me.fruitman.sprint.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.fruitman.sprint.presentation.SprintApplication

@Database(
    version = 5,
    entities = [Task::class, Action::class],
    autoMigrations = [AutoMigration (from = 4, to = 5)],
    exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun actionDao(): ActionDao
}

val database = Room.databaseBuilder(
    SprintApplication.instance,
    AppDatabase::class.java, "task-database")
    .build()
