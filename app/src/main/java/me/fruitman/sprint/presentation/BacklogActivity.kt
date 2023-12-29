package me.fruitman.sprint.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.fruitman.sprint.R
import me.fruitman.sprint.databinding.ActivityBacklogBinding
import me.fruitman.sprint.databinding.ActivityEditTaskBinding
import me.fruitman.sprint.domain.entities.Stage

class BacklogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBacklogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBacklogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(
                R.id.frame,
                TaskListFragment().apply {
                    arguments = Bundle().apply {
                        putString("stage_name", Stage.Backlog.name) } } )
        }

        supportActionBar?.title = getString(R.string.backlog)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val newTaskButton: FloatingActionButton = binding.fabNewTask
        newTaskButton.setOnClickListener {
            startActivity(
                Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("taskId", 0)
                }
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}