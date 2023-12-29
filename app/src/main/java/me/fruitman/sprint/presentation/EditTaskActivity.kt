package me.fruitman.sprint.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import me.fruitman.sprint.R
import me.fruitman.sprint.databinding.ActivityEditTaskBinding

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
        private lateinit var fragment: EditTaskFragment

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.edit_task, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem) = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addMenuProvider(menuProvider)

        fragment = EditTaskFragment().apply {
            arguments = Bundle().apply {
                putInt("taskId", intent.getIntExtra("taskId", 0))
            }
        }
        supportFragmentManager.commit {
            replace(
                R.id.frame,
                fragment,
                "EditTaskFragment" )
        }

        supportActionBar?.title = getString(R.string.edit_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_save -> {
                fragment.onClickSave()
                return true
            }
            R.id.action_trash -> {
                fragment.onClickDelete()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}