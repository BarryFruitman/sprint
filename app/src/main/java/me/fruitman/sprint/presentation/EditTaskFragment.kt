package me.fruitman.sprint.presentation

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import me.fruitman.sprint.R
import me.fruitman.sprint.databinding.ActionListItemBinding
import me.fruitman.sprint.databinding.FragmentEditTaskBinding
import me.fruitman.sprint.domain.entities.Stage
import me.fruitman.sprint.util.ArgumentsViewModelProviderFactory
import me.fruitman.sprint.util.replacedWhere
import java.util.Collections

class EditTaskFragment : Fragment() {
    private val viewModel: EditTaskViewModel by viewModels { ArgumentsViewModelProviderFactory(arguments) }
    private lateinit var binding: FragmentEditTaskBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentEditTaskBinding.inflate(inflater, container, false).apply {
            model = viewModel
            lifecycleOwner = viewLifecycleOwner
            titleEdit.requestFocus()
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter
        val adapter = ActionAdapter()
        binding.actionList.layoutManager = LinearLayoutManager(requireContext())
        binding.actionList.adapter = adapter

        binding.stageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioBacklog -> Stage.Backlog.name
                R.id.radioTodo -> Stage.ThisWeek.name
                R.id.radioInProgress -> Stage.Today.name
                R.id.radioFollowUp -> Stage.WaitingOn.name
                R.id.radioDone -> Stage.Done.name
                else -> throw ArrayIndexOutOfBoundsException()
            }.let { stageName ->
                viewModel.onStageChanged(stageName)
            }
        }

        // Drag and drop
        val callback: ItemTouchHelper.Callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.actionList)

        // View model observers
        viewModel.open.observe(viewLifecycleOwner) { isOpen ->
            // Open by default
            if (!isOpen) requireActivity().finish()
        }
        viewModel.actions.observe(viewLifecycleOwner) { actions ->
            val diffCallback = ActionDiffCallback(oldActionList = adapter.actions, newActionList = actions)
            adapter.actions = actions

            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(adapter)
        }
        viewModel.selectedStage.observe(viewLifecycleOwner) { selectedStage->
            when (selectedStage) {
                Stage.Backlog.name -> binding.radioBacklog.isChecked = true
                Stage.ThisWeek.name -> binding.radioTodo.isChecked = true
                Stage.Today.name -> binding.radioInProgress.isChecked = true
                Stage.WaitingOn.name -> binding.radioFollowUp.isChecked = true
                Stage.Done.name -> binding.radioDone.isChecked = true
            }

            when {
                selectedStage != Stage.Backlog.name -> binding.radioBacklog.isChecked = false
                selectedStage != Stage.ThisWeek.name -> binding.radioTodo.isChecked = false
                selectedStage != Stage.Today.name -> binding.radioInProgress.isChecked = false
                selectedStage != Stage.WaitingOn.name -> binding.radioFollowUp.isChecked = false
                selectedStage != Stage.Done.name -> binding.radioDone.isChecked = false
            }
        }

        viewModel.onViewReady()
    }

    fun onClickSave() = viewModel.onClickSave()

    fun onClickDelete() = viewModel.onClickDelete()

    private inner class ActionAdapter : RecyclerView.Adapter<ActionViewHolder>() {
        var actions: List<EditTaskViewModel.ActionItem> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ActionViewHolder(
                actionId = -1,
                binding = ActionListItemBinding.inflate(layoutInflater)).apply {
                    binding.description.doAfterTextChanged { text ->
                        val action = actions.first { actionId == it.id }
                        val newAction = action.copy(description = text.toString())
                        actions = actions.replacedWhere(newAction) { it == action } // this is a hack

                        viewModel.onActionChanged(newAction)
                    }
                    binding.done.setOnCheckedChangeListener { _, isChecked ->
                        binding.description.paintFlags = if (isChecked) Paint.STRIKE_THRU_TEXT_FLAG else 0

                        val action = actions.first { actionId == it.id }
                        viewModel.onActionChanged(action.copy(done = isChecked))
                    }
                }

        override fun getItemCount() = actions.size

        override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
            holder.apply {
                actionId = actions[position].id
                binding.description.setText(actions[position].description)
                holder.binding.done.isChecked = actions[position].done
            }
        }

        fun onRowMoved(fromViewHolder: ViewHolder, toViewHolder: ViewHolder) {
            val fromPosition = fromViewHolder.adapterPosition
            val toPosition = toViewHolder.adapterPosition

            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(actions, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(actions, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    private class ActionViewHolder(var actionId: Int, val binding: ActionListItemBinding) : ViewHolder(binding.root)

    class ActionDiffCallback(
        private val oldActionList: List<EditTaskViewModel.ActionItem>,
        private val newActionList: List<EditTaskViewModel.ActionItem>) : DiffUtil.Callback() {

        override fun getOldListSize() = oldActionList.size

        override fun getNewListSize() = newActionList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldActionList[oldItemPosition].id == newActionList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldActionList[oldItemPosition].description == newActionList[newItemPosition].description
                && oldActionList[oldItemPosition].done == newActionList[newItemPosition].done
    }

    private class ItemMoveCallback(val adapter: ActionAdapter) : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
            adapter.onRowMoved(viewHolder, target)
            return true
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) = Unit

        override fun isLongPressDragEnabled() = true

        override fun isItemViewSwipeEnabled() = false
    }
}
