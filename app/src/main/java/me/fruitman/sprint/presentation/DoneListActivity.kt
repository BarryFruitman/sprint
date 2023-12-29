package me.fruitman.sprint.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import me.fruitman.sprint.R
import me.fruitman.sprint.databinding.ActivityEditTaskBinding
import me.fruitman.sprint.domain.entities.Stage

class DoneListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(
                R.id.frame,
                TaskListFragment().apply {
                    arguments = Bundle().apply {
                        putString("stage_name", Stage.Done.name) } } )
        }

        supportActionBar?.title = getString(R.string.done)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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