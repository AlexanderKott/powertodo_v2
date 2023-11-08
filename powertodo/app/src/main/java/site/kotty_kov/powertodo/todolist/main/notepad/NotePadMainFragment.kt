package site.kotty_kov.powertodo.todolist.main.notepad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.NotepadApplicationFragmentBinding
import site.kotty_kov.powertodo.databinding.NotepadBottomSheetBinding
import site.kotty_kov.powertodo.todolist.main.common.*
import site.kotty_kov.powertodo.todolist.main.common.utils.collapseBottom
import site.kotty_kov.powertodo.todolist.main.common.utils.hideKeyboard
import site.kotty_kov.powertodo.todolist.main.common.utils.setClipboard
import site.kotty_kov.powertodo.todolist.main.data.notepad.NoteItem
import site.kotty_kov.powertodo.todolist.main.notepad.recycler.NotepadSwipeHelper
import site.kotty_kov.powertodo.todolist.main.viewModel.CommonViewModel
import site.kotty_kov.powertodo.todolist.main.viewModel.NotePadViewModel
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel


class NotePadMainFragment() : Fragment() {
    private lateinit var undoredohelper: UndoRedoHelper
    private lateinit var urStore : UndoRedoStore
    private val gson =  Gson()

    private val viewModelNotePadViewModel: NotePadViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    private val vmCommonViewModel: CommonViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    private val viewModelToDoViewModel: ToDoViewModel by viewModels(
        ownerProducer = { this.requireActivity() })



    private lateinit var radapter: NoteItemsAdapter
    private lateinit var binding: NotepadApplicationFragmentBinding

    private var exitState: Int = 0  //display placeholder or list


    companion object {
        private const val PLACEHOLDER  = 1
        private const val EDIT = 2
        private const val UNDOPREFIX = "undoFile"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return NotepadApplicationFragmentBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = NotepadApplicationFragmentBinding.bind(view)

        initRecycler(binding)
        initBottomNavigation(binding)
        iniListSwitcher(binding)

    }



    private fun iniListSwitcher(binding: NotepadApplicationFragmentBinding) {
        viewModelNotePadViewModel.notesItemsCount.observe(viewLifecycleOwner, { state ->
            //page to display
            binding.viewflipper.displayedChild = state

            //what to show when user clicks (exit\save)
            exitState = state

            //animation
            handleAnimation(state, binding)
        })
    }

    private fun handleAnimation(
        state: Int?,
        binding: NotepadApplicationFragmentBinding
    ) {
        if (state == PLACEHOLDER) {
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
    }



    private fun initBottomNavigation(binding: NotepadApplicationFragmentBinding) {
        with(binding.bottomNavigationN) {

            //SAVE. save note
            save.setOnClickListener {
                binding.viewflipper.displayedChild = exitState
                listNotesG.visibility = View.VISIBLE
                editNoteG.visibility = View.GONE
                undoredohelper.clearHistory()
                saveRecordToDB(binding)

            }

            //notes list menu
            menu2.setOnClickListener {
                popupMenu(it, R.menu.notepad_list_menu)
            }


            // new / edit note
            enterNewn.setOnClickListener {
                urStore = UndoRedoStore()
                setNoteEditMode(binding, null )
            }

            //Menu in notepad
            menu.setOnClickListener {
                popupMenu(it, R.menu.notepad_editor_menu)
            }


            undo.setOnClickListener {
                if (undoredohelper.canUndo) {
                    undoredohelper.undo()
                }
                checkUndoRedoBtnsState()
            }

            redo.setOnClickListener {
                if (undoredohelper.canRedo) {
                    undoredohelper.redo()
                }
                checkUndoRedoBtnsState()
            }


            binding.linedEdit.addTextChangedListener {
                if (::undoredohelper.isInitialized){
                    checkUndoRedoBtnsState()
                }
            }


            //copy feature
            copy.isCheckable = false
            copy.setOnClickListener {
                with(binding.linedEdit) {
                    val selectedText = text?.toString()?.substring(selectionStart, selectionEnd) ?: ""
                    if (selectionStart != -1 && selectionEnd != -1 && selectedText.trim() != "") {
                        setClipboard(requireContext(), selectedText)
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.textCopied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


            //common
            close.setOnClickListener {
                collapseBottom(binding.bottomNavigationN.bottomSheetND)
                viewModelToDoViewModel.saveColorButtonsState()
                requestPasswordPage()
                requireActivity().moveTaskToBack(true)
            }

        }

    }

    private fun NotepadBottomSheetBinding.checkUndoRedoBtnsState() {
        redo.isChecked = undoredohelper.canRedo
        undo.isChecked = undoredohelper.canUndo
    }


    private fun saveRecordToDB(binding: NotepadApplicationFragmentBinding) {
        if (binding.linedEdit.text.toString().trim() != "") {
            undoredohelper.storePersistentState(urStore, UNDOPREFIX)
            viewModelNotePadViewModel.upsert(
                viewModelNotePadViewModel.getRecordForEdit()
                    .copy(text = binding.linedEdit.text.toString(),
                    undoHistory = gson.toJson(urStore))
            )

            binding.linedEdit.setText("")
            binding.linedEdit.hideKeyboard()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModelNotePadViewModel.getRecordForEdit().text?.let { textFromRecord ->
            val editorText = binding.linedEdit.text.toString()
            if (editorText.trim() != "" && textFromRecord != editorText) {
                saveRecordToDB(binding)

                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.textSaved),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setNoteEditMode(binding: NotepadApplicationFragmentBinding, item : NoteItem?) {
        with(binding.bottomNavigationN) {
            undoredohelper = UndoRedoHelper(binding.linedEdit)
            undoredohelper.setMaxHistorySize(100)

            item?.undoHistory?.let{
                urStore =  gson.fromJson(it, UndoRedoStore::class.java)
                undoredohelper.restorePersistentStateFromStr(urStore , UNDOPREFIX)
            }
            checkUndoRedoBtnsState()
            binding.viewflipper.displayedChild = EDIT
            listNotesG.visibility = View.GONE
            editNoteG.visibility = View.VISIBLE
            vmCommonViewModel.setEditNotepadRecord()
        }
    }


    fun popupMenu(view: View, resId: Int) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menuInflater.inflate(resId, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.notepad -> {
                    requestNotepadPage()
                    true
                }

                R.id.notepad_exit -> {
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




    private fun initRecycler(binding: NotepadApplicationFragmentBinding) {
        radapter = NoteItemsAdapter(object : NotepadActionsListener {

            //edit when user click record
            override fun cardViewLongClick(item: NoteItem) {
                viewModelNotePadViewModel.markRecordForEdit(item)
                binding.linedEdit.setText(item.text)
                setNoteEditMode(binding, item)

            }
        })

        binding.notesRecycler.layoutManager = LinearLayoutManager(activity)
        binding.notesRecycler.adapter = radapter
        binding.notesRecycler.itemAnimator?.addDuration = 0


        val itemTouchHelper = ItemTouchHelper(object : NotepadSwipeHelper(binding.notesRecycler) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                val cancelButton = cancelButton(position)
                val deleteButton = deleteButton(position, viewModelNotePadViewModel)
                val emptyButton = emptyButton(position, viewModelNotePadViewModel)

                return listOf(cancelButton, deleteButton, emptyButton)
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.notesRecycler)

        viewModelNotePadViewModel.notesItems.observe(viewLifecycleOwner, { posts ->
            radapter.submitList(posts)
            viewModelNotePadViewModel.setToDoItemsCount(posts.size)
        })
    }


    private fun cancelButton(position: Int): NotepadSwipeHelper.UnderlayButton {
        return NotepadSwipeHelper.UnderlayButton(
            requireContext(),
            requireContext().getString(R.string.cancel),
            14.0f,
            android.R.color.darker_gray,
            object : NotepadSwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                }
            })
    }


    private fun deleteButton(
        position: Int,
        vm: NotePadViewModel
    ): NotepadSwipeHelper.UnderlayButton {
        return NotepadSwipeHelper.UnderlayButton(
            requireContext(),
            requireContext().getString(R.string.delete_are_u_sure),
            12.0f,
            android.R.color.holo_red_light,
            object : NotepadSwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    vm.deleteItem(radapter.getItemById(position))
                }
            })
    }

    //hack
    private fun emptyButton(
        position: Int,
        vm: NotePadViewModel
    ): NotepadSwipeHelper.UnderlayButton {
        return NotepadSwipeHelper.UnderlayButton(
            requireContext(),
            ".                           .",
            14.0f,
            android.R.color.white,
            object : NotepadSwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {

                }
            })
    }



}