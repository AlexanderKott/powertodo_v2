package site.kotty_kov.powertodo.todolist.main.todo.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.AddnewitemFragmentBinding
import site.kotty_kov.powertodo.todolist.main.data.TodoItem
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.viewModel.CommonViewModel


class AddEditItemFragment : DialogFragment() {
    private var requestKey = Values.newFragmentBundleKey
    private lateinit var binding: AddnewitemFragmentBinding
    private var updateItemId: Long = 0L
    private var elapsedTime: Int = 0

    private val viewModel: CommonViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = AddnewitemFragmentBinding.inflate(requireActivity().layoutInflater)

        var colorForNewItem = 0

        //new case
        arguments?.let { bundle ->
            bundle.getInt(Values.newitemKey).let{
                colorForNewItem = it
            }
        }

        //edit case
        arguments?.let { bundle ->
            bundle.getString(Values.itemToEditKey)?.let {
                val trObject: TodoItem? =
                    Utils.gsonParser?.fromJson(it, TodoItem::class.java)

                trObject?.let {
                    binding.addTodoItemsText.setText(trObject.text)
                    updateItemId = trObject.number
                    colorForNewItem = trObject.color
                    binding.checkBoxManyLines.isGone = true
                    elapsedTime = trObject.elapsedtime
                    requestKey = Values.editFragmentBundleKey
                }

            }
        }


        //Dialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(binding.root)
            .setPositiveButton(R.string.ok) { dialog, which ->
               if  (viewModel.getApplicationState() != Values.LOCKED){
                result(colorForNewItem)
               }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }

        val dialogWindow = builder.create()
        dialogWindow.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialogWindow
    }


    private fun result(color: Int) {
        setFragmentResult(
            Values.EditItemDialogFragmentAKey,
            bundleOf(
                requestKey to
                        Utils.gsonParser?.toJson(
                            ToDoItemTransfer(
                                updateItemId,
                                binding.addTodoItemsText.text.toString(),
                                elapsedTime,
                                binding.checkBoxManyLines.isChecked,
                                color
                            )
                        )
            )
        )
    }


    override fun onResume() {
        super.onResume()
        val window = dialog!!.window
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        window.setLayout((size.x * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)

        binding.addTodoItemsText.requestFocus()
        requireDialog().getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

    }


}