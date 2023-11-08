package site.kotty_kov.powertodo.todolist.main.todo.view

import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.PopupMenu
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.FragmentToDoBinding
import site.kotty_kov.powertodo.todolist.main.common.*
import site.kotty_kov.powertodo.todolist.main.common.utils.collapseBottom
import site.kotty_kov.powertodo.todolist.main.data.TodoItem
import site.kotty_kov.powertodo.todolist.main.todo.view.recycler.ActionsListener
import site.kotty_kov.powertodo.todolist.main.todo.view.recycler.ItemsAdapter
import site.kotty_kov.powertodo.todolist.main.todo.view.recycler.SwipeItemHelper
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel


class ToDoFragment : Fragment(R.layout.fragment_to_do) {

    private val viewModelToDoViewModel: ToDoViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    private lateinit var recycler: RecyclerView
    private lateinit var rAdapter: ItemsAdapter
    private var color = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentToDoBinding.bind(view)

        initRecycler(viewModelToDoViewModel, binding)
        iniListSwitcher(viewModelToDoViewModel, binding)
        initBottomNavigation(binding, viewModelToDoViewModel)
    }

    private fun iniListSwitcher(viewModelToDoViewModel: ToDoViewModel, binding: FragmentToDoBinding) {
        viewModelToDoViewModel.toDoItemsCount.observe(viewLifecycleOwner, { value ->
            binding.switcher.displayedChild = value
            if (value == 1) {
                binding.textEmpty.isGone = false
                binding.placeholder.isGone = false

                val inAnim = AlphaAnimation(0f, 1f)
                inAnim.duration = 1700
                binding.textEmpty.animation = inAnim
                binding.placeholder.animation = inAnim
            } else {
                binding.textEmpty.animation = null
                binding.textEmpty.isGone = true
                binding.placeholder.animation = null
                binding.placeholder.isGone = true
            }
        })
    }


    private fun initBottomNavigation(
        binding: FragmentToDoBinding,
        viewModelToDoViewModel: ToDoViewModel
    ) {
        with(binding.bottomNavigationTD) {
            viewModelToDoViewModel.colour.observe(viewLifecycleOwner, { clr ->
                color = clr
                when (clr) {
                    0 -> pad1.isChecked = true
                    1 -> pad2.isChecked = true
                    2 -> pad3.isChecked = true
                    3 -> pad4.isChecked = true
                }
            })


            //Enter new Item
            enterNew.setOnClickListener {
                activity?.supportFragmentManager?.let {
                    AddEditItemFragment()
                        .apply {
                            arguments = bundleOf().apply {
                                putInt("Newitem", color)
                            }
                            show(it, "")
                        }
                }
            }

            //Menu button
            menu.setOnClickListener {
                popupMenu(it)

            }

            clearDone.setOnClickListener {
                viewModelToDoViewModel.clearDoneItems()
            }

            close.setOnClickListener {
                viewModelToDoViewModel.saveColorButtonsState()
                collapseBottom(binding.bottomNavigationTD.bottomSheetTD)
                requestPasswordPage()
                requireActivity().moveTaskToBack(true)
            }


            pad1.setOnClickListener {
                updateColor(it, viewModelToDoViewModel, binding)
            }

            pad2.setOnClickListener {
                updateColor(it, viewModelToDoViewModel, binding)
            }

            pad3.setOnClickListener {
                updateColor(it, viewModelToDoViewModel, binding)
            }

            pad4.setOnClickListener {
                updateColor(it, viewModelToDoViewModel, binding)
            }


            val gestures =
                GesturesListenerImpl(this@ToDoFragment, binding.bottomNavigationTD.bottomSheetTD)
            binding.bottomNavigationTD.pad1
                .setOnTouchListener(gestures)
            binding.bottomNavigationTD.pad2
                .setOnTouchListener(gestures)
            binding.bottomNavigationTD.pad3
                .setOnTouchListener(gestures)
            binding.bottomNavigationTD.pad4
                .setOnTouchListener(gestures)

        }

        //перебросить запросы из child фрагментов в родительский фрагмент
        childFragmentManager.setFragmentResultListener(Values.switchPagerKey,
                                                        viewLifecycleOwner) { key, bundle ->
            setFragmentResult(key, bundle)
        }


    }


    fun popupMenu(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menuInflater.inflate(R.menu.todo_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.notepad -> {
                    requestNotepadPage()
                    true
                }
                R.id.todo -> {
                    requestToDoPage()
                    true
                }
                R.id.setup -> {
                    requestSettings()
                    true
                }

                R.id.about -> {
                    About.showAbout(requireContext())
                    true
                }

                else -> {
                }
            }
            true
        }
        popupMenu.show()
    }


    private fun updateColor(
        it: View,
        viewModelToDoViewModel: ToDoViewModel,
        binding: FragmentToDoBinding
    ) {
        viewModelToDoViewModel.setColorToItemsReturn(Integer.parseInt(it.tag as String))
        with(binding.bottomNavigationTD) {
            viewModelToDoViewModel.prepareButtonsStateToSave(
                pad1.isChecked,
                pad2.isChecked,
                pad3.isChecked,
                pad4.isChecked
            )
        }
    }

    private fun initRecycler(viewModelToDoViewModel: ToDoViewModel, binding: FragmentToDoBinding) {
        val adapter = ItemsAdapter(object : ActionsListener {
            override fun cardViewLongClick(todoitem: TodoItem) {
                activity?.supportFragmentManager?.let {
                    AddEditItemFragment().apply {
                        arguments = bundleOf(
                            Values.itemToEditKey to Utils.gsonParser?.toJson(todoitem)
                        )
                        show(it, "")
                    }
                }
            }

            override fun doneCheckboxClick(todoitem: TodoItem) {
                viewModelToDoViewModel.updateItem(todoitem)
            }
        })

        recycler = binding.todoListRecycler
        rAdapter = adapter
        binding.todoListRecycler.layoutManager = LinearLayoutManager(activity)
        binding.todoListRecycler.adapter = adapter
        recycler.itemAnimator?.addDuration = 0

        val sih = object : SwipeItemHelper(requireContext(),
            object : OnSwipeToDoListener {
                override fun onItemDelete(pos: Int) {
                    viewModelToDoViewModel.deleteItem(adapter.getItemById(pos))
                }

                override fun isItemAllowedToSwipe(pos: Int): Boolean {
                    return true
                }

                override fun onItemSchedule(pos: Int) {
                    viewModelToDoViewModel.scheduleItem(adapter.getItemById(pos))
                }
            }) {}


        sih.leftBG = ContextCompat.getColor(requireContext(), R.color.red)
        sih.leftLabel = getString(R.string.delete)
        sih.leftIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_close_24)

        //configure right swipe
        sih.rightBG = ContextCompat.getColor(requireContext(), R.color.purple_200)
        sih.rightLabel = getString(R.string.schedule)
        sih.rightIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_forward)

        val touchHelper = ItemTouchHelper(sih)
        touchHelper.attachToRecyclerView(binding.todoListRecycler)

        viewModelToDoViewModel.toDoItems.observe(viewLifecycleOwner, { posts ->
            val count = adapter.itemCount
            adapter.submitList(posts)
            viewModelToDoViewModel.setToDoItemsCount(posts.size)
            if (posts.size > count) {
                recycler.postDelayed(Runnable {
                    recycler.smoothScrollToPosition(0)
                }, 700)
            }
        })


    }


}


