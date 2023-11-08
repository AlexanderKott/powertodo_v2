package site.kotty_kov.powertodo.todolist.main.done

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.FragmentDoneBinding
import site.kotty_kov.powertodo.todolist.main.common.*
import site.kotty_kov.powertodo.todolist.main.common.utils.collapseBottom
import site.kotty_kov.powertodo.todolist.main.todo.view.Utils
import site.kotty_kov.powertodo.todolist.main.todo.view.recycler.SwipeItemHelper
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel


class DoneFragment : Fragment(), DoneItemsCallBack {

    private lateinit var binding : FragmentDoneBinding
    private val viewModelToDoViewModel: ToDoViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    private var itemsList: List<DoneRecord>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentDoneBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDoneBinding.bind(view)

        initBottomNavigation(binding, viewModelToDoViewModel)

        //init recycler
        val recyclerAdapter = DoneAdapter(this as DoneItemsCallBack)

        val sih = object : SwipeItemHelper(requireContext(),
            object : OnSwipeToDoListener {
                override fun onItemDelete(pos: Int) {
                    if (recordIsNotNull(recyclerAdapter, pos)) {
                        viewModelToDoViewModel.deleteDoneItem(recyclerAdapter.getItemById(pos))
                    } else {
                        recyclerAdapter.notifyItemChanged(pos)
                    }
                }

                override fun isItemAllowedToSwipe(pos: Int) :Boolean {
                       return recordIsNotNull(recyclerAdapter, pos)
                }

                override fun onItemSchedule(pos: Int) {
                    if (recordIsNotNull(recyclerAdapter, pos)) {
                        viewModelToDoViewModel.scheduleDoneItem(recyclerAdapter.getItemById(pos))
                        recyclerAdapter.notifyItemChanged(pos)
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.scheduledagain),
                            Toast.LENGTH_LONG
                        ).show()

                } else {
                    recyclerAdapter.notifyItemChanged(pos)
                }
            }

            }) {}


        sih.leftBG = ContextCompat.getColor(requireContext(), R.color.red)
        sih.leftLabel = getString(R.string.delete)
        sih.leftIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_close_24)

        //configure right swipe
        sih.rightBG = ContextCompat.getColor(requireContext(), R.color.gray)
        sih.rightLabel = getString(R.string.scheduleAgain)
        sih.rightIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_forward)

        sih.compactMode = true


        val touchHelper = ItemTouchHelper(sih)


        binding.doneRecycler.apply {
            adapter = recyclerAdapter
            touchHelper.attachToRecyclerView(this)

            addItemDecoration(HeaderItemDecoration(
                this,
                shouldFadeOutHeader = true
            ) {
                recyclerAdapter.getItemViewType(it) == R.layout.done_header_layout
            })
        }

        viewModelToDoViewModel.doneItems.observe(viewLifecycleOwner, Observer { lst ->
            itemsList = prepareItems(lst)
            recyclerAdapter.submitList(itemsList)
        })



        binding.bottomNavigationD.share.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(requireContext().getString(R.string.jsonDialogCaption))
                .setMessage(requireContext().getString(R.string.jsonDialogBody))
                .setPositiveButton(android.R.string.ok) { a, b ->
                     PlainTextSharingHandler.share(requireContext(),
                        Utils.gsonParser?.toJson(itemsList) ?: "[]")
                }
                .setNegativeButton(android.R.string.cancel) { a, b ->}
                .setIcon(android.R.drawable.ic_menu_share)
                .show()
        }
    }

    private fun recordIsNotNull(recyclerAdapter: DoneAdapter, pos: Int): Boolean {
       return recyclerAdapter.getItemById(pos).record != null
    }


    override fun getAllDoneItems(): List<DoneRecord>? {
        return itemsList
    }


    private fun initBottomNavigation(
        binding: FragmentDoneBinding,
        viewModelToDoViewModel: ToDoViewModel
    ) {
            with(binding.bottomNavigationD) {
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

                close.setOnClickListener {
                    viewModelToDoViewModel.saveColorButtonsState()
                    collapseBottom(binding.bottomNavigationD.bottomSheetD)
                    requestPasswordPage()
                    requireActivity().moveTaskToBack(true)
                }

                val gestures = GesturesListenerImpl(this@DoneFragment, binding.bottomNavigationD.bottomSheetD)

                binding.bottomNavigationD.pad1
                    .setOnTouchListener(gestures)
                binding.bottomNavigationD.pad2
                    .setOnTouchListener(gestures)
                binding.bottomNavigationD.pad3
                    .setOnTouchListener(gestures)
                binding.bottomNavigationD.pad4
                    .setOnTouchListener(gestures)

                //перебросить запросы из child фрагментов в родительский фрагмент
                childFragmentManager.setFragmentResultListener(
                     Values.switchPagerKey,
                    viewLifecycleOwner
                ) { key, bundle ->
                    setFragmentResult(key, bundle)
                }



        }


        binding.bottomNavigationD.notepad.setOnClickListener {
            collapseBottom(binding.bottomNavigationD.bottomSheetD)
            requestNotepadPage()
        }
    }




    private fun View.setMarginBottom(b: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, b)
        layoutParams = params
    }

    //Change landscape/portrait details - add or remove bottomsheet
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.bottomNavigationD.bottomSheetD.visibility = View.GONE
            binding.doneRecycler.setMarginBottom(0)
        } else
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding.bottomNavigationD.bottomSheetD.visibility = View.VISIBLE
            context?.resources?.getDimension(R.dimen.bottomNav)?.toInt()?.let {
                binding.doneRecycler?.setMarginBottom(it)
            }
        }

    }

    private fun updateColor(
        it: View,
        viewModelToDoViewModel: ToDoViewModel,
        binding: FragmentDoneBinding
    ) {
        binding.bottomNavigationD?.let { bottom ->
            viewModelToDoViewModel.setColorToItemsReturn(Integer.parseInt(it.tag as String))
            with(binding.bottomNavigationD) {
                viewModelToDoViewModel.prepareButtonsStateToSave(
                    pad1.isChecked,
                    pad2.isChecked,
                    pad3.isChecked,
                    pad4.isChecked
                )
            }
        }
    }


}