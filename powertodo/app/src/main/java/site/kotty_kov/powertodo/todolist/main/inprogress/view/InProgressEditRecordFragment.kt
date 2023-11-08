package site.kotty_kov.powertodo.todolist.main.inprogress.view

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.FragmentInprogressChildEditRecordBinding
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.done.PlainTextSharingHandler
import site.kotty_kov.powertodo.todolist.main.inprogress.timeToReadableFormat
import site.kotty_kov.powertodo.todolist.main.inprogress.timeValues
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel


class InProgressEditRecordFragment : Fragment() {
    private lateinit var binding: FragmentInprogressChildEditRecordBinding

    private val viewModelToDoViewModel: ToDoViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    private var elapsedTimeT = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInprogressChildEditRecordBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //load data
        viewModelToDoViewModel.getInProgressTempItem()?.let {
            binding.acceptanceCriteria.setText(it.acceptance_criteria)
            binding.detailsEdit.setText(it.text)
            binding.taskNumber.setText("#${it.number}")
            elapsedTimeT = it.etime
            binding.timeTrack.setText(getString(R.string.elapsedtime,
                timeToReadableFormat(it.etime)))
        }

        binding.recordButtonDone.setOnClickListener {
            //save
            saveDataToDB()
            //and back
            setFragmentResult(
                Values.InProgressEditKey,
                bundleOf(Values.EditRecordDoneKey to "1")
            )
        }

        binding.delegate.setOnClickListener {
            saveDataToDB()

            viewModelToDoViewModel.getInProgressTempItem()?.let {
                PlainTextSharingHandler.share(
                    requireContext(),
                    PlainTextSharingHandler.formatInprogressItem(
                        requireContext(), it.copy(
                            text = binding.detailsEdit.text.toString(),
                            acceptance_criteria = binding.acceptanceCriteria.text.toString(),
                            etime = elapsedTimeT
                        )
                    )
                )
            }
        }


        binding.timeTrack.setOnClickListener {
            var choise = 0;

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.elapsedtime, timeToReadableFormat(elapsedTimeT)))
                .setSingleChoiceItems(R.array.log_time, 0) { dialog, input ->
                    choise = input
                }
                .setPositiveButton(android.R.string.ok) { a, b ->
                    when (choise) {
                        6, 7, 8, 9, 10 -> if (elapsedTimeT - timeValues[choise] < 0) {
                            elapsedTimeT = 0
                        } else {
                            elapsedTimeT -= timeValues[choise]
                        }
                        else -> {
                            elapsedTimeT += timeValues[choise]
                        }
                    }
                    binding.timeTrack.setText(getString(R.string.elapsedtime, timeToReadableFormat(elapsedTimeT)))
                }
                .setNegativeButton(android.R.string.cancel) { a, b ->
                }
                .show()
        }


    }



    private fun saveDataToDB() {
        viewModelToDoViewModel.getInProgressTempItem()?.let {
            viewModelToDoViewModel.updateInProgressItem(
                it.copy(
                    etime = elapsedTimeT,
                    text = binding.detailsEdit.text.toString(),
                    acceptance_criteria = binding.acceptanceCriteria.text.toString()
                )
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        viewModelToDoViewModel.getInProgressTempItem()?.let {
            viewModelToDoViewModel.markInProgressItemAsTemp(
                it.copy(
                    etime = elapsedTimeT,
                    text = binding.detailsEdit.text.toString(),
                    acceptance_criteria = binding.acceptanceCriteria.text.toString()
                )
            )
        }
        super.onConfigurationChanged(newConfig)
    }
}