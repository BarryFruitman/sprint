@file:Suppress("DEPRECATION")

package me.fruitman.sprint.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import me.fruitman.sprint.domain.entities.Stage

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val tabStages = listOf(Stage.ThisWeek, Stage.Today, Stage.WaitingOn)
    private val tabFragments: List<Fragment> = tabStages.map { stage ->
        TaskListFragment().apply {
            arguments = Bundle().apply {
                putString("stage_name", stage.name)
            }
        }
    }

    override fun getItem(position: Int) = tabFragments[position]

    override fun getPageTitle(position: Int): CharSequence {
        return tabStages[position].title
    }

    override fun getCount() =  tabStages.size
}