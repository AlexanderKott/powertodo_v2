package site.kotty_kov.powertodo.todolist.main.common

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.setFragmentResult
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.common.loading.LoadingFragment
import site.kotty_kov.powertodo.todolist.main.common.login.LoginFragment
import site.kotty_kov.powertodo.todolist.main.common.preferences.SettingsFragment
import site.kotty_kov.powertodo.todolist.main.inprogress.view.InProgressEditRecordFragment
import site.kotty_kov.powertodo.todolist.main.inprogress.view.InProgressSetTimerFragment
import site.kotty_kov.powertodo.todolist.main.inprogress.view.InprogressFragmentChildList
import site.kotty_kov.powertodo.todolist.main.notepad.NotePadMainFragment
import site.kotty_kov.powertodo.todolist.main.todo.view.ToDoMainFragment
import site.kotty_kov.powertodo.todolist.main.viewModel.CommonViewModel


//------------- Set InProgress child fragments from Main InProgress Fragment ---------------
fun Fragment.displayRecordEdit(vm: CommonViewModel) {
    childFragmentManager.commit {
        setReorderingAllowed(true)
        replace<InProgressEditRecordFragment>(
            R.id.child_fragment_container,
            "EditInProgressFragment"
        )
    }
    vm.setLastInPorgressScreenStateAsRecordEdit()
}

fun Fragment.displayTimerEdit(vm: CommonViewModel) {
    childFragmentManager.commit {
        setReorderingAllowed(true)
        replace<InProgressSetTimerFragment>(
            R.id.child_fragment_container,
            "SetTimerInProgressFragment"
        )
    }
      vm.setLastInPorgressScreenStateAsTitmer()
}

  fun Fragment.displayList(vm: CommonViewModel) {
    childFragmentManager.commit {
        replace<InprogressFragmentChildList>(R.id.child_fragment_container)
        setReorderingAllowed(true)
    }
    vm.setLastInPorgressScreenStateAsList()
}


// ---------------------------- InProgress back to List--------------------------------------
fun Fragment.backToList(vm: CommonViewModel) {
    childFragmentManager.commit {
        setReorderingAllowed(true)
        replace<InprogressFragmentChildList>(
            R.id.child_fragment_container
        )
    }
    vm.setLastInPorgressScreenStateAsList()
}




//--------------------------- Popup Menu requests for Main Activity -------------------------
fun Fragment.requestSettings() {
     setFragmentResult(
        Values.drawerMenuKey,
        bundleOf(Values.btn_menuKey to Values.settingsKey)
    )
}


fun Fragment.requestToDoPage() {
     setFragmentResult(
        Values.drawerMenuKey,
        bundleOf(Values.btn_menuKey to Values.todoKey)
    )
}

fun Fragment.requestNotepadPage() {
     setFragmentResult(
        Values.drawerMenuKey,
        bundleOf(Values.btn_menuKey to Values.notepadKey)
    )
}

fun Fragment.requestPasswordPage() {
    setFragmentResult(
        Values.drawerMenuKey,
        bundleOf(Values.btn_menuKey to Values.passwordPage)
    )
}


//-----------------------------Main activity sets fragments: ---------------------------------------


fun AppCompatActivity.requestTodoFragment(vm: CommonViewModel, page : Int = 0) {
    supportFragmentManager.commit {
        replace<ToDoMainFragment>(R.id.child, "ToDoMainFragment", bundleOf("page" to page))
        setReorderingAllowed(true)

    }
    vm.setAppStateToDo()
    requestUnspecified()
}

fun AppCompatActivity.requestNotepadFragment(vm: CommonViewModel, editNote: Boolean = false) {
    supportFragmentManager.commit {
        replace<NotePadMainFragment>(R.id.child,"NotePadMainFragment")
        setReorderingAllowed(true)
    }
    vm.setStateNotepad()
    requestUnspecified()
}


fun AppCompatActivity.requestLoadingFragment() {
    supportFragmentManager.commit {
        replace<LoadingFragment>(R.id.child)
        setReorderingAllowed(true)
    }
    requestUnspecified()
}



fun AppCompatActivity.requestPasswordFragment(vm: CommonViewModel,) {
    supportFragmentManager.commit {
        replace<LoginFragment>(R.id.child)
        setReorderingAllowed(true)
    }
    vm.setPasswordLockScr()
    requestPortrait()
}


  fun AppCompatActivity.requestSettingsFragment(vm: CommonViewModel) {
    supportFragmentManager.commit {
        replace<SettingsFragment>(R.id.child, Values.settingsFragment)
        addToBackStack("SettingsFragment")
        setReorderingAllowed(true)

    }
      vm.setSettings()
    requestPortrait()
}







// ----- other ----------------------------------
private fun AppCompatActivity.requestUnspecified() {
    this.setRequestedOrientation(
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    )
}

private fun AppCompatActivity.requestPortrait() {
    this.setRequestedOrientation(
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    )
}