package site.kotty_kov.powertodo.todolist.main.common


object Values{

enum class States {
    NOT_INITIALIZED, NO_PASSWORD, IS_PROTECTED
}


    const val  LOCKED = 2

    const val channelId = "Alarms"
    val colours = arrayOf("#9CBDFF","#E4BEFF","#A8FFB4","#FFAAAA")

    //to do capitalize constants
    const val passwordPage = "passwordPage"
    const val RecordEditKey = "RecordEdit"
    const val TimerEditKey ="TimerEdit"
    const val TimerEditDoneKey ="TimerEditDone"
    const val InProgressEditKey = "InProgressEdit"
    const val EditRecordDoneKey = "EditRecordDone"
    const  val switchPagerKey = "switchPager"
    const  val drawerMenuKey = "drawerMenu"
    const  val btn_menuKey = "btn_menu"
    const  val notepadKey = "notepad"
    const  val switchRightKey = "switchRight"
    const  val switchLeftKey = "switchLeft"
    const  val stateKey = "state"
    const  val todoKey = "todo"
    const  val settingsKey = "settings"
    const  val itemToEditKey  = "itemToEdit"
    const  val reminderKey = 1111
    const  val timersWereRestKey = "123"
    const  val EditItemDialogFragmentAKey = "EditItemDialogFragmentA"
    const  val newFragmentBundleKey = "newFragmentBundle"
    const  val editFragmentBundleKey = "editFragmentBundle"
    const  val newitemKey = "Newitem"
    const  val settingsFragment = "settingsFragment"

    //Password template
    const val defaultPassword = "00000" //template and length!
    const val passwordTemp = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    const val crownChar = 0x1F451
    const val unlock = "unlock"



    const val lockTime = 60 * 1000

    const val exitTime = 4 * 1000

    const val TIME_UNIT: Int = 60 * 1000

    //timer intervals
    val arraySpinnerValues = arrayOf(1, 5, 7, 10 ,15, 20, 30, 45 , 60 , 120, 180)




    ////////---------------------------------------------------------------------------------
    data class TimerDataStorage(
        val mode: Int,
        val repeat: Boolean,
        val days:Array<Boolean>,
        val time: String,
        val timeSpinnerPosition: Int
    )
}