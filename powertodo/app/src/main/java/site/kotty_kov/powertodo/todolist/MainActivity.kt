package site.kotty_kov.powertodo.todolist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.ActivityMainBinding
import site.kotty_kov.powertodo.todolist.main.common.*
import site.kotty_kov.powertodo.todolist.main.viewModel.ViewModelFactory
import site.kotty_kov.powertodo.todolist.main.viewModel.CommonViewModel
import site.kotty_kov.powertodo.todolist.main.viewModel.NotePadViewModel
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var viewModelToDoViewModel: ToDoViewModel
    private lateinit var vmCommon: CommonViewModel

    private var firstTime: Boolean = true
    private var lockApp: Values.States = Values.States.NOT_INITIALIZED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(App.getApp().getDatabase())

        vmCommon =
            ViewModelProviders.of(this, factory).get(CommonViewModel::class.java)

        viewModelToDoViewModel =
            ViewModelProviders.of(this, factory).get(ToDoViewModel::class.java)

        val viewModelNotepad =
            ViewModelProviders.of(this, factory).get(NotePadViewModel::class.java)


        vmCommon.checkPassword()

        vmCommon.isAppProtected().observe(this, { isPasswordProtected ->
            lockApp = if (isPasswordProtected) Values.States.IS_PROTECTED else Values.States.NO_PASSWORD
        })


        //листенер ивентов от чилдов для открытия меню
        supportFragmentManager
            .setFragmentResultListener(Values.drawerMenuKey, this) { _, bundle ->
                bundle.getString(Values.btn_menuKey).let {
                    when (it) {
                        Values.todoKey -> {
                            requestTodoFragment(vmCommon)
                        }
                        Values.notepadKey -> {
                            requestNotepadFragment(vmCommon)
                        }
                        Values.settingsKey -> {
                            requestSettingsFragment(vmCommon)
                        }
                        Values.passwordPage -> {
                            if (lockApp == Values.States.IS_PROTECTED) {
                                requestPasswordFragment(vmCommon)
                            }
                        }

                    }
                }
            }

        //ивент на Unlock
        supportFragmentManager
            .setFragmentResultListener(Values.unlock, this) { _, bundle ->
                requestTodoFragment(vmCommon)
            }

        requestLoadingFragment()
    }


    private var appLockTime: Long = 0
    override fun onStart() {
        super.onStart()
        when (lockApp) {

            Values.States.NOT_INITIALIZED -> {
                firstTimeRun()
            }

            Values.States.IS_PROTECTED -> {
                when {
                    appLockTime == 0L -> {
                        requestPasswordFragment(vmCommon)
                    }
                    appLockTime < System.currentTimeMillis() - Values.lockTime -> {
                        requestPasswordFragment(vmCommon)
                    }
                    else -> {
                        //do nothing
                    }
                }
            }

            Values.States.NO_PASSWORD -> {
                if (firstTime) {
                    requestTodoFragment(vmCommon)
                    firstTime = false
                }
            }
        }
    }

    private fun firstTimeRun() {
        val runable = object : Runnable {
            override fun run() {
                when (lockApp) {
                    Values.States.NO_PASSWORD -> requestTodoFragment(vmCommon)
                    Values.States.IS_PROTECTED -> requestPasswordFragment(vmCommon)
                    Values.States.NOT_INITIALIZED -> Handler(Looper.getMainLooper()).postDelayed(this, 300)
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed(runable, 350)
    }


    override fun onPause() {
        appLockTime = System.currentTimeMillis()
        viewModelToDoViewModel.saveColorButtonsState()
        super.onPause()
    }


    private var lastBackPressTime: Long = 0
    override fun onBackPressed() {
        if (supportFragmentManager.fragments.last().tag == Values.settingsFragment) {
            supportFragmentManager.popBackStack()
            return
        }
        when (vmCommon.getApplicationState()) {
            4 -> {
                requestNotepadFragment(vmCommon)
                return
            }

            5, 6 -> {
                requestTodoFragment(vmCommon, 1)
                return
            }
        }

        if (lastBackPressTime < System.currentTimeMillis() - Values.exitTime) {
            Toast.makeText(this, this.getString(R.string.confirmExit), Toast.LENGTH_SHORT)
                .show()
            lastBackPressTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

}