package me.fruitman.sprint.presentation

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.DragEvent.ACTION_DRAG_ENTERED
import android.view.DragEvent.ACTION_DRAG_EXITED
import android.view.DragEvent.ACTION_DROP
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnDragListener
import android.view.Window
import androidx.activity.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import me.fruitman.sprint.R
import me.fruitman.sprint.databinding.ActivityMainBinding
import me.fruitman.sprint.domain.entities.Stage

class MainActivity : AppCompatActivity() {

    private val viewModel: MainTabViewModel by viewModels()
    private val onDragListener = OnDragTaskListener()
    private lateinit var binding: ActivityMainBinding
    val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.task_list, menu)

            binding.toolbar.getChildAt(1).setOnDragListener(onDragListener)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            startActivity(
                Intent(this@MainActivity, DoneListActivity::class.java).apply {
                    putExtra("taskId", 0)
                }
            )

            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addMenuProvider(menuProvider)
        setSupportActionBar(binding.toolbar)

        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        binding.tabs.getTabAt(0)?.view?.setOnDragListener(onDragListener)
        binding.tabs.getTabAt(1)?.view?.setOnDragListener(onDragListener)
        binding.tabs.getTabAt(2)?.view?.setOnDragListener(onDragListener)

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener {
            startActivity(
                Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("taskId", 0)
                }
            )
        }
    }

    inner class OnDragTaskListener : OnDragListener {

        override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
            when (dragEvent?.action) {
                ACTION_DRAG_ENTERED -> {
                    view?.foregroundTintList = ColorStateList.valueOf(Color.RED)
                    view?.invalidate()
//                    view?.setBackgroundColor(Color.LTGRAY)
                }
                ACTION_DRAG_EXITED -> {
//                    view?.setBackgroundColor(Color.WHITE)
                    view?.foregroundTintList = null
                    view?.invalidate()
                }
                ACTION_DROP -> {
                    view?.foregroundTintList = null
                    view?.invalidate()

                    var iTab = 0
                    if (view == binding.toolbar.getChildAt(1)) {
                        iTab = binding.tabs.tabCount
                    } else {
                        for (n in 0 until binding.tabs.tabCount) {
                            if (binding.tabs.getTabAt(n)?.view == view) {
                                iTab = n
                                break
                            }
                        }
                    }

                    val stage = sectionsPagerAdapter.tabStages.getOrNull(iTab) ?: Stage.Done
                    viewModel.onDragToNewColumn(dragEvent.clipData.getItemAt(0).text.toString().toInt(), stage)
//                    view?.cancelDragAndDrop()
//                    view?.setBackgroundColor(Color.WHITE)
                }
            }

            return true
        }

    }
}