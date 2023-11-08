package site.kotty_kov.powertodo.todolist.main.inprogress.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel
import android.view.animation.AlphaAnimation
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.*
import androidx.recyclerview.widget.DividerItemDecoration
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.FragmentInprogressChildListBinding
import site.kotty_kov.powertodo.todolist.main.common.*
import site.kotty_kov.powertodo.todolist.main.common.utils.collapseBottom
import site.kotty_kov.powertodo.todolist.main.data.InProgressItem
import site.kotty_kov.powertodo.todolist.main.inprogress.view.recycler.InProgressSwipeHelper
import site.kotty_kov.powertodo.todolist.main.todo.view.recycler.*

class InprogressFragmentChildList : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var rAdapter: InProgressItemsAdapter

    private val viewModelToDoViewModel: ToDoViewModel by viewModels(
        ownerProducer = { this.requireActivity() })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentInprogressChildListBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInprogressChildListBinding.bind(view)

        initRecycler(viewModelToDoViewModel, binding)
        iniListSwitcher(viewModelToDoViewModel, binding)
        initBottomNavigation(binding, viewModelToDoViewModel)
    }

    private fun iniListSwitcher(
        viewModelToDoViewModel: ToDoViewModel,
        binding: FragmentInprogressChildListBinding
    ) {
        viewModelToDoViewModel.inProgressItemsCount.observe(viewLifecycleOwner, { value ->
            binding.switcher.displayedChild = value
            if (value == 1) {
                binding.textEmpty1.isGone = false
                binding.placeholder.isGone = false

                val inAnim = AlphaAnimation(0f, 1f)
                inAnim.duration = 1700
                binding.textEmpty1.animation = inAnim
                binding.placeholder.animation = inAnim
            } else {
                binding.textEmpty1.animation = null
                binding.textEmpty1.isGone = true
                binding.placeholder.animation = null
                binding.placeholder.isGone = true
            }
        })
    }

    private fun initBottomNavigation(
        binding: FragmentInprogressChildListBinding,
        viewModelToDoViewModel: ToDoViewModel
    ) {
        with(binding.bottomNavigationX) {
            viewModelToDoViewModel.colour.observe(viewLifecycleOwner, { color ->
                when (color) {
                    0 -> pad1.isChecked = true
                    1 -> pad2.isChecked = true
                    2 -> pad3.isChecked = true
                    3 -> pad4.isChecked = true
                }
            })


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



            //set swipe gesture for color buttons
            val gestures = GesturesListenerImpl(this@InprogressFragmentChildList, binding.bottomNavigationX.bottomSheetX )
            binding.bottomNavigationX.pad1
                .setOnTouchListener(gestures)
            binding.bottomNavigationX.pad2
                .setOnTouchListener(gestures)
            binding.bottomNavigationX.pad3
                .setOnTouchListener(gestures)
            binding.bottomNavigationX.pad4
                .setOnTouchListener(gestures)


            binding.bottomNavigationX.notepad.setOnClickListener {
                collapseBottom(binding.bottomNavigationX.bottomSheetX)
                requestNotepadPage()
            }

            close.setOnClickListener {
                viewModelToDoViewModel.saveColorButtonsState()
                collapseBottom(binding.bottomNavigationX.bottomSheetX)
                requestPasswordPage()
                requireActivity().moveTaskToBack(true)
            }

            menu.setOnClickListener {
                popupMenu(it)
            }
        }




        binding.bottomNavigationX.notepad.setOnClickListener {
            collapseBottom(binding.bottomNavigationX.bottomSheetX)
            requestNotepadPage()
        }


        //перебросить запросы из child фрагментов в родительский фрагмент
        childFragmentManager.setFragmentResultListener(
            Values.switchPagerKey,
            viewLifecycleOwner
        ) { key, bundle ->
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

                R.id.about -> {
                    About.showAbout(requireContext())
                    true
                }

                R.id.setup -> {
                    requestSettings()
                    true
                }

                else -> {
                    false
                }
            }
            true
        }
        popupMenu.show()
    }





    private fun updateColor(
        it: View,
        viewModelToDoViewModel: ToDoViewModel,
        binding:FragmentInprogressChildListBinding
    ) {
        viewModelToDoViewModel.setColorToItemsReturn(Integer.parseInt(it.tag as String))
            with(binding.bottomNavigationX) {
                viewModelToDoViewModel.prepareButtonsStateToSave(
                    pad1.isChecked,
                    pad2.isChecked,
                    pad3.isChecked,
                    pad4.isChecked
                )
        }
    }






    private fun initRecycler(viewModelToDoViewModel: ToDoViewModel, binding: FragmentInprogressChildListBinding) {
        val adapter = InProgressItemsAdapter(object : InProgressActionsListener {

            override fun cardViewLongClick(item: InProgressItem) {
                viewModelToDoViewModel.markInProgressItemAsTemp(item)
                setFragmentResult(
                    Values.InProgressEditKey,
                    bundleOf(Values.RecordEditKey to "1")
                )
            }

            override fun setTimerCheckboxClick(item: InProgressItem) {
                viewModelToDoViewModel.markInProgressItemAsTemp(item)
                setFragmentResult(
                    Values.InProgressEditKey,
                    bundleOf(Values.TimerEditKey to "1")
                )
            }
        })

        recycler = binding.inProgressListRecycler
        rAdapter = adapter
        binding.inProgressListRecycler.layoutManager = LinearLayoutManager(activity)
        binding.inProgressListRecycler.adapter = adapter

        recycler.addItemDecoration(DividerItemDecoration(requireContext(),
            DividerItemDecoration.VERTICAL))
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelper = ItemTouchHelper(object : InProgressSwipeHelper(recycler) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val cancelButton = cancelButton(position)
                val failButton = markAsFailButton(position, viewModelToDoViewModel)
                val doneButton = markAsDoneButton(position, viewModelToDoViewModel)
                buttons = listOf(doneButton, failButton, cancelButton)
                return buttons
            }
        })

        itemTouchHelper.attachToRecyclerView(recycler)


        viewModelToDoViewModel.inProgressItems.observe(viewLifecycleOwner, { posts ->
            adapter.submitList(posts)
            viewModelToDoViewModel.setInProgressItemsCount(posts.size)

        })

    }


    private fun toast(text: String) {
       Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun cancelButton(position: Int) : InProgressSwipeHelper.UnderlayButton {
        return InProgressSwipeHelper.UnderlayButton(
            requireContext(),
            requireContext().getString(R.string.cancel),
            14.0f,
            android.R.color.darker_gray,
            object : InProgressSwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                }
            })
    }

    private fun markAsFailButton(position: Int, vm :ToDoViewModel) : InProgressSwipeHelper.UnderlayButton {
        return InProgressSwipeHelper.UnderlayButton(
            requireContext(),
            requireContext().getString(R.string.markAsFail) ,
            14.0f,
            android.R.color.holo_red_light,
            object : InProgressSwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    vm.markItemDone(rAdapter.getItemById(position), false)
                    toast(requireContext().getString(R.string.itemMarkedasFailed))
                }
            })
    }

    private fun markAsDoneButton(position: Int, vm :ToDoViewModel) : InProgressSwipeHelper.UnderlayButton {
        return InProgressSwipeHelper.UnderlayButton(
            requireContext(),
              requireContext().getString(R.string.markAsDone),
            14.0f,
            android.R.color.holo_green_light,
            object : InProgressSwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    vm.markItemDone(rAdapter.getItemById(position), true)
                    toast(requireContext().getString(R.string.ItemMarkedAsDone))
                }
            })
    }

}

