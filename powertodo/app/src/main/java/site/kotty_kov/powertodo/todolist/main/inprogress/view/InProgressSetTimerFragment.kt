package site.kotty_kov.powertodo.todolist.main.inprogress.view

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.FragmentInprogressChildSetTimerBinding
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.inprogress.NotificationHelper
import site.kotty_kov.powertodo.todolist.main.inprogress.TimeCalculator
import site.kotty_kov.powertodo.todolist.main.todo.view.Utils
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel
import java.util.*


class InProgressSetTimerFragment : Fragment() {
    private lateinit var binding: FragmentInprogressChildSetTimerBinding

    private val viewModelToDoViewModel: ToDoViewModel by viewModels(
        ownerProducer = { this.requireActivity() })
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInprogressChildSetTimerBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            binding.timePicker.setIs24HourView(true)
        }
        else {
            binding.timePicker.setIs24HourView(false)
        }


        binding.timersDontWorkInfo.tag = "btn"
        binding.timersDontWorkInfo.setOnClickListener {


            AlertDialog.Builder(requireContext())
                .setTitle(requireContext().getString(R.string.TimersInfo))
                .setMessage(requireContext().getString(R.string.TimersText))
                .setPositiveButton(android.R.string.ok) { a, b ->
                }
                .setNeutralButton(R.string.setups){ a, b ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package",
                            requireContext().getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)

                }

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }


        //Load data to form
        loadDataToForm(viewModelToDoViewModel)

        //Save data from form
        saveDataListeners(viewModelToDoViewModel)

        setupFormListeners()


    }

    private fun saveDataListeners(viewModelToDoViewModel: ToDoViewModel) {
        with(binding) {

            timerEditDone.setOnClickListener {
                val mode = getMode()
                 val obj = Values.TimerDataStorage(
                    mode,
                    repeat = repeat.isChecked,
                    days = arrayOf(
                        sun.isChecked,
                        mon.isChecked,
                        tue.isChecked,
                        wed.isChecked,
                        thu.isChecked,
                        fr.isChecked,
                        sat.isChecked
                    ),
                    time = "${timePicker.currentHour}:${timePicker.currentMinute}",
                    timeSpinnerPosition = intervalspinner.selectedItemPosition
                )

                viewModelToDoViewModel.getInProgressTempItem()?.let {
                    it.timerSet = mode != 0
                    it.scheduled = Utils.gsonParser?.toJson(obj).toString()
                    it.whenTimerSet = if (mode == 1) {
                        TimeCalculator.shortTimerGetTime(obj, System.currentTimeMillis())
                    } else if (mode == 2) {
                        val calendar = Calendar.getInstance()
                        val time = TimeCalculator.longTimerGetTime(obj.time, obj.days, calendar)
                        if (time > 0) {

                            AlertDialog.Builder(requireContext())
                                .setMessage(requireContext().getString(R.string.timerset,
                                    Date(time).toString()))
                                .setPositiveButton(android.R.string.ok) { a, b ->
                                }
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show()


                            time
                        } else {
                            Toast.makeText(requireContext(),
                                requireContext().getString(R.string.timerisNotset),
                                Toast.LENGTH_LONG).show()
                            it.timerSet = false
                            -1
                        }

                    } else {
                        it.timerSet = false
                        -1
                    }


                    viewModelToDoViewModel.updateInProgressItem(it)

                    NotificationHelper.scheduleNotification(
                        requireContext(), 3000, NotificationHelper.TServiceState.USER_RUN
                    )
                }

                setFragmentResult(
                    Values.InProgressEditKey,
                    bundleOf(Values.TimerEditDoneKey to "1")
                )

            }


        }
    }





    //-----------------------------------------------------


    override fun onConfigurationChanged(newConfig: Configuration) {
        saveValuesToTempItem()
        super.onConfigurationChanged(newConfig)
    }

    private fun saveValuesToTempItem() {
        with(binding) {
            val obj = Values.TimerDataStorage(
                0,
                repeat = repeat.isChecked,
                days = arrayOf(
                    sun.isChecked,
                    mon.isChecked,
                    tue.isChecked,
                    wed.isChecked,
                    thu.isChecked,
                    fr.isChecked,
                    sat.isChecked
                ),
                time = "${timePicker.currentHour}:${timePicker.currentMinute}",
                timeSpinnerPosition = intervalspinner.selectedItemPosition
            )
            Utils.gsonParser?.toJson(obj)?.let {
                viewModelToDoViewModel.inProgressTempItemAddValue(it) }
        }
    }


    private fun loadDataToForm(viewModelToDoViewModel: ToDoViewModel) {
        with(binding) {
            markViewsDisabled()

            if (viewModelToDoViewModel.getInProgressTempItem()?.scheduled.isNullOrBlank()) {
                radioDisabled.isChecked = true
            }

            //load data if it is
            viewModelToDoViewModel.getInProgressTempItem()?.let {

                Utils.gsonParser?.fromJson(it.scheduled, Values.TimerDataStorage::class.java)
                    ?.let { o ->
                        mon.isChecked = o.days[1]
                        tue.isChecked = o.days[2]
                        wed.isChecked = o.days[3]
                        thu.isChecked = o.days[4]
                        fr.isChecked = o.days[5]
                        sat.isChecked = o.days[6]
                        sun.isChecked = o.days[0]

                        when (o.mode) {
                            1 -> {
                                radioMinOrHours.isChecked = true
                                showRadioMinHoursViews()
                            }
                            2 -> {
                                radioDays.isChecked = true
                                showRadioDaysView()
                            }
                            else -> radioDisabled.isChecked = true
                        }
                        val hm = o.time.split(":").map { it -> it.toInt() }
                        timePicker.currentHour = hm[0]
                        timePicker.currentMinute = hm[1]
                        repeat.isChecked = o.repeat
                        intervalspinner.post(Runnable {
                            intervalspinner.setSelection(o.timeSpinnerPosition) })
                    }
            }
        }
    }

    private fun getMode(): Int {
        with(binding) {
            if (radioDisabled.isChecked) return 0
            if (radioMinOrHours.isChecked) return 1
            if (radioDays.isChecked) return 2
        }
        return 0
    }


    private fun setupFormListeners() {
        with(binding) {
            //Listeners
            radioDisabled.setOnClickListener {
                radioMinOrHours.isChecked = false
                radioDays.isChecked = false
                markViewsDisabled()
            }
            radioMinOrHours.setOnClickListener {
                radioDisabled.isChecked = false
                radioDays.isChecked = false
                markViewsDisabled()
                showRadioMinHoursViews()
            }
            radioDays.setOnClickListener {
                radioMinOrHours.isChecked = false
                radioDisabled.isChecked = false
                markViewsDisabled()
                showRadioDaysView()
            }

        }
    }

    private fun FragmentInprogressChildSetTimerBinding.showRadioDaysView() {
        mon.isEnabled = true
        tue.isEnabled = true
        wed.isEnabled = true
        thu.isEnabled = true
        fr.isEnabled = true
        sat.isEnabled = true
        sun.isEnabled = true
        timePicker.isEnabled = true
    }

    private fun FragmentInprogressChildSetTimerBinding.markViewsDisabled() {
         layout1.children.forEach { if (it.tag == null) it.isEnabled = false }
    }

    private fun FragmentInprogressChildSetTimerBinding.showRadioMinHoursViews() {
        intervalspinner.isEnabled = true
        repeat.isEnabled = true
    }

}
