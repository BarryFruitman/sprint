package me.fruitman.sprint.presentation

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.fruitman.sprint.databinding.FragmentTaskListBinding
import me.fruitman.sprint.databinding.TaskListItemBinding
import me.fruitman.sprint.util.ArgumentsViewModelProviderFactory
import java.util.Collections

class TaskListFragment : Fragment() {

    private val viewModel: TaskListViewModel by viewModels { ArgumentsViewModelProviderFactory(arguments) }
    private var binding: FragmentTaskListBinding? = null
    private lateinit var taskListAdapter: TaskListAdapter

    class TaskItemViewHolder(val binding: TaskListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            taskListAdapter = TaskListAdapter()

            binding?.taskList?.apply {
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
                adapter = taskListAdapter
            }

            viewModel.tasks.observe(viewLifecycleOwner) { newTaskList ->
                taskListAdapter.onNewTaskList(newTaskList)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    inner class TaskListAdapter : RecyclerView.Adapter<TaskItemViewHolder>() {
        private var taskList = emptyList<TaskListViewModel.TaskItemModel>()
        private val callback: ItemTouchHelper.Callback = ItemMoveCallback()
        private val touchHelper = ItemTouchHelper(callback)

        init {
            touchHelper.attachToRecyclerView(binding?.taskList)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
            val binding = TaskListItemBinding.inflate(layoutInflater).apply {
                applyClickListenerForDragAndDrop()
            }

            return TaskItemViewHolder(binding)
        }

        override fun getItemCount() = taskList.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
            holder.binding.apply {
                model = taskList[position]
                root.tag = taskList[position].task.id.toString()
                listDragTouchTarget.setOnClickListener {
                    // Navigation hack
                    startActivity(
                        Intent(activity, EditTaskActivity::class.java).apply {
                            putExtra("taskId", viewModel.tasks.value?.get(position)?.task?.id)
                        }
                    )
                }

                holder.binding.dragReorder.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> touchHelper.startDrag(holder)
                    }
                    false
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun onNewTaskList(newTaskList: List<TaskListViewModel.TaskItemModel>) {
            taskList = newTaskList
            notifyDataSetChanged() // Use DiffUtil
        }

        private fun TaskListItemBinding.applyClickListenerForDragAndDrop() {
            listDragTouchTarget.setOnLongClickListener {
                val item = ClipData.Item(root.tag as? CharSequence)
                val dragData = ClipData(
                    (root.tag as? String),
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item)

                val myShadow = DragTaskShadowBuilder(this.root)

                root.startDragAndDrop(dragData, myShadow, null, 0)

                true
            }
        }

        fun onRowMoved(fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder) {
            val fromPosition = binding?.taskList?.getChildAdapterPosition(fromViewHolder.itemView) ?: return
            val toPosition = binding?.taskList?.getChildAdapterPosition(toViewHolder.itemView) ?: return

            if (fromPosition == -1 || toPosition == -1) return

            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(taskList, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(taskList, i, i - 1)
                }
            }

            notifyItemMoved(fromPosition, toPosition)
        }

        inner class ItemMoveCallback : ItemTouchHelper.Callback() {
            private var prevActionState: Int = ItemTouchHelper.ACTION_STATE_IDLE

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                onRowMoved(viewHolder, target)
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && prevActionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewModel.onListOrderChanged(taskList)
                }
                prevActionState = actionState
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

            override fun isLongPressDragEnabled() = false

            override fun isItemViewSwipeEnabled() = false
        }

    }
}
