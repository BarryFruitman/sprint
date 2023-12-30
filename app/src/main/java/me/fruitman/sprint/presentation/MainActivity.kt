package me.fruitman.sprint.presentation

import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import me.fruitman.sprint.R
import me.fruitman.sprint.databinding.ActivityMainBinding
import me.fruitman.sprint.databinding.LayoutBottomNavigationBinding
import me.fruitman.sprint.domain.entities.Stage

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    inner class OnDragTaskListener(val stageName: String) : View.OnDragListener {
        override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
            if (dragEvent?.action == DragEvent.ACTION_DROP) {
                Log.d("DRAG_DROP_DEBUG", "Dropped on $stageName")
                viewModel.onDragToNewColumn(
                    dragEvent.clipData.getItemAt(0).text.toString().toInt(),
                    Stage.fromName(stageName) ?: return true
                )
            }

            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DRAG_DROP_DEBUG", "testing 1 2 3")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sprintFragment = SprintFragment()
        val backlogFragment = TaskListFragment().apply {
            arguments = Bundle().apply {
                putString("stage_name", Stage.Backlog.name) } }
        val doneFragment = TaskListFragment().apply {
            arguments = Bundle().apply {
                putString("stage_name", Stage.Done.name) } }

        val buttonBinding: LayoutBottomNavigationBinding = binding.bottomNavigation
        buttonBinding.bottomNavBacklog.apply {
            setOnDragListener(OnDragTaskListener(Stage.Backlog.name))
            setOnClickListener {
                supportFragmentManager.commit {
                    replace(R.id.main_content, backlogFragment)
                }
            }
        }

        buttonBinding.bottomNavSprint.apply {
            setOnDragListener(OnDragTaskListener(Stage.ThisWeek.name))
            setOnClickListener {
                supportFragmentManager.commit {
                    replace(R.id.main_content, sprintFragment)
                }
            }
        }

        buttonBinding.bottomNavDone.apply {
            setOnDragListener(OnDragTaskListener(Stage.Done.name))
            setOnClickListener {
                supportFragmentManager.commit {
                    replace(R.id.main_content, doneFragment)
                }
            }
        }

        supportFragmentManager.commit {
            replace(R.id.main_content, sprintFragment)
        }
    }
}