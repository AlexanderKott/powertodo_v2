package site.kotty_kov.powertodo.todolist.main.inprogress.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import site.kotty_kov.powertodo.databinding.FragmentInprogressBinding
import site.kotty_kov.powertodo.todolist.main.common.*
import site.kotty_kov.powertodo.todolist.main.viewModel.CommonViewModel

class InprogressFragment : Fragment() {


    private val vmCommonViewModel: CommonViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentInprogressBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInprogressBinding.bind(view)

        when (vmCommonViewModel.getApplicationState()) {
            7 -> displayList(vmCommonViewModel)
            5 -> displayTimerEdit(vmCommonViewModel)
            6 -> displayRecordEdit(vmCommonViewModel)
            else -> { displayList(vmCommonViewModel) }
        }


        //перебросить запросы из child фрагментов в родительский фрагмент
        childFragmentManager.setFragmentResultListener(
            Values.switchPagerKey,
            viewLifecycleOwner
        ) { key, bundle ->
            setFragmentResult(key, bundle)
        }

        childFragmentManager.setFragmentResultListener(
            Values.drawerMenuKey,
            viewLifecycleOwner
        ) { key, bundle ->
            setFragmentResult(key, bundle)
        }


        //------------------------------------------------------------------------
        //Listeners
        //show children fragments
        childFragmentManager
            .setFragmentResultListener(Values.InProgressEditKey, this) { _, bundle ->
                bundle.getString(Values.TimerEditKey)?.let {
                    displayTimerEdit(vmCommonViewModel)
                }

                bundle.getString(Values.RecordEditKey)?.let {
                    displayRecordEdit(vmCommonViewModel)
                }


                ///  Callbacks from child fragments
                bundle.getString(Values.TimerEditDoneKey)?.let {
                    backToList(vmCommonViewModel)
                }

                bundle.getString(Values.EditRecordDoneKey)?.let {
                    backToList(vmCommonViewModel)
                }


            }
    }

}










