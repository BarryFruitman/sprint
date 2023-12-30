package me.fruitman.sprint.presentation

import android.os.Bundle
import android.view.DragEvent
import android.view.DragEvent.ACTION_DROP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.fruitman.sprint.databinding.FragmentSprintBinding
import me.fruitman.sprint.domain.entities.Stage

class SprintFragment : Fragment() {
    private val viewModel: SprintViewModel by viewModels()
    private val onDragListener = OnDragTaskListener()
    private var _binding: FragmentSprintBinding? = null
    private val binding get() = _binding!!
    private val viewPager: SectionsPagerAdapter
        get() = binding.viewPager.adapter as SectionsPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSprintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = SectionsPagerAdapter(childFragmentManager)
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        binding.tabs.getTabAt(0)?.view?.setOnDragListener(onDragListener)
        binding.tabs.getTabAt(1)?.view?.setOnDragListener(onDragListener)
        binding.tabs.getTabAt(2)?.view?.setOnDragListener(onDragListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class OnDragTaskListener : View.OnDragListener {
        override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
            if (dragEvent?.action == ACTION_DROP) {
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

                val stage = viewPager.tabStages.getOrNull(iTab) ?: Stage.Done
                viewModel.onDragToNewColumn(
                    dragEvent.clipData.getItemAt(0).text.toString().toInt(),
                    stage
                )
            }

            return true
        }
    }
}