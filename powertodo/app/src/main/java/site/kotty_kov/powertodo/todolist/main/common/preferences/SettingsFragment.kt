package site.kotty_kov.powertodo.todolist.main.common.preferences

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.preference.*
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.data.user.UserInfo
import site.kotty_kov.powertodo.todolist.main.inprogress.getSound
import site.kotty_kov.powertodo.todolist.main.viewModel.CommonViewModel


class SettingsFragment : PreferenceFragmentCompat() {

    private var passwordField: EditTextPreference? = null
    private var passwordSwitch: SwitchPreferenceCompat? = null
    private var timeCCheckBox: CheckBoxPreference? = null
    private var tZoneCCheckBox: CheckBoxPreference? = null
    private var DateCCheckBox: CheckBoxPreference? = null
    private var soundT: ListPreference? = null
    private var soundT2: Preference? = null

    private lateinit var oldPassword: String

   private var defaultRingtone : Ringtone? = null

    private val viewModelCommonViewModel: CommonViewModel by viewModels(
        ownerProducer = { this.requireActivity() })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModelCommonViewModel.getPassword().observe(viewLifecycleOwner, { value ->
            oldPassword = value
            passwordSwitch?.isChecked = oldPassword != Values.defaultPassword
        })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        //Load / save data from/to shared prefs
        val shPref = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        passwordField = findPreference<EditTextPreference>("setPassword")
        passwordSwitch = findPreference<SwitchPreferenceCompat>("usePassword")
        timeCCheckBox = findPreference<CheckBoxPreference>("time")
        tZoneCCheckBox = findPreference<CheckBoxPreference>("tZone")
        DateCCheckBox = findPreference<CheckBoxPreference>("DateC")
        soundT = findPreference<ListPreference>("soundT")
        soundT2 = findPreference<Preference>("customSound")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            soundT2?.isVisible = true
        } else {
            soundT?.isVisible = true
        }

        //Views
        soundT2?.let { p ->
            p.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationManager: NotificationManager =
                        requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val intent =
                        Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName())
                                putExtra(Settings.EXTRA_CHANNEL_ID,
                                    notificationManager
                                    .getNotificationChannel(Values.channelId).id)
                        }
                    startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "Only for new devices", Toast.LENGTH_LONG)
                            .show()
                    }
                    true
                }
        }



        soundT?.let { p ->
            p.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference: Preference, any: Any ->
                    //save
                    shPref.edit().apply {
                        putInt("alarmSound", (any as String).toInt())
                        apply()
                    }
                    demoSound(any)
                    true
                }
        }

        DateCCheckBox?.let { p ->
            p.isChecked = shPref.getBoolean("dateChangedNotify", true)
            p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //save
                shPref.edit().apply {
                    putBoolean("dateChangedNotify", p.isChecked)
                    apply()
                }

                true
            }
        }

        tZoneCCheckBox?.let { p ->
            p.isChecked = shPref.getBoolean("timezoneNotify", true)
            p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //save
                shPref.edit().apply {
                    putBoolean("timezoneNotify", p.isChecked)
                    apply()
                }
                true
            }
        }

        timeCCheckBox?.let { p ->
            p.isChecked = shPref.getBoolean("timeChangedNotify", false)
            p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //save
                shPref.edit().apply {
                    putBoolean("timeChangedNotify", p.isChecked)
                    apply()
                }
                true
            }
        }


        passwordSwitch?.let { p ->
            p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                p.isChecked = oldPassword != Values.defaultPassword

                if (p.isChecked) {
                    p.isChecked = !requestPrevPasswordAndSaveIt(oldPassword, shPref, null)

                } else {
                    displayAlert(
                        R.string.passwordCaption,
                        TextO(R.string.passwordSetFirst),
                        null, null, null
                    )
                    p.isChecked = false
                }

                true
            }
        }



        passwordField?.let { field ->
            field.text = ""

            field.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference: Preference, data: Any ->
                    val enteredPassword = (data as String)
                    field.text = ""

                    //Short password filter
                    if (enteredPassword.length != Values.defaultPassword.length) {
                        displayAlert(
                            R.string.passwordCaption,
                            TextO(R.string.passwordDosentFit),
                            null,
                            null,
                            null
                        )
                        return@OnPreferenceChangeListener false
                    }


                    //YOU CANNOT SET 0000
                    if (enteredPassword == Values.defaultPassword) {
                        displayAlert(
                            R.string.passwordCaption,
                            TextO(R.string.passwordYouCannot),
                            null,
                            null,
                            null
                        )
                        return@OnPreferenceChangeListener false

                    } else {//SET NEW PASSWORD CASE
                        //Just set password
                        if (oldPassword == Values.defaultPassword) {
                            savePassword(shPref, data)
                        } else { //request prev password before set
                            requestPrevPasswordAndSaveIt(oldPassword, shPref, data)
                        }
                    }

                    field.text = ""
                    true
                }


            //add constrains to password field
            field.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf<InputFilter>(LengthFilter(Values.defaultPassword.length))
                editText.setText("")
            }
        }
    }

    private fun demoSound(any: Any) {
        val uri = when ((any as String).toInt()) {
            0 -> {
                Toast.makeText(requireContext(), R.string.noSound, Toast.LENGTH_LONG)
                    .show()
                getSound(requireContext(), RingtoneManager.TYPE_NOTIFICATION)
            }
             1 -> {
                Toast.makeText(requireContext(), R.string.justDefaultBeep, Toast.LENGTH_LONG)
                    .show()
                getSound(requireContext(), RingtoneManager.TYPE_NOTIFICATION)
            }
            2 -> getSound(requireContext(), R.raw.xilo)
            3 -> getSound(requireContext(), R.raw.bells)
            4 -> getSound(requireContext(), R.raw.guitar)
            5 -> getSound(requireContext(), R.raw.harp)
            else -> getSound(requireContext(), R.raw.empty)
        }

        defaultRingtone = if (defaultRingtone == null) {
            RingtoneManager.getRingtone(context, uri)
        } else {
            defaultRingtone!!.stop()
            RingtoneManager.getRingtone(context, uri)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            defaultRingtone?.isLooping = false
        }
        defaultRingtone?.play()
    }


    override fun onStop() {
        super.onStop()
        defaultRingtone?.stop()
    }

    private fun requestPrevPasswordAndSaveIt(
        oldPassword: String?,
        shPref: SharedPreferences,
        data: Any?
    ): Boolean {
        var result = false

        displayAlert(
            R.string.passwordCaption,
            TextO(R.string.passwordTypePrevPassword),
            EditText(requireContext()).also {  // add a password field
                it.inputType = InputType.TYPE_CLASS_NUMBER
                it.filters = arrayOf<InputFilter>(LengthFilter(Values.defaultPassword.length))
            },

            { passValue ->
                result = if (oldPassword == passValue) {
                    savePassword(shPref, data)
                    true

                } else { //prev password is wrong
                    displayAlert(
                        R.string.passwordCaption,
                        TextO(R.string.passwordIsWrong),
                        null, null, null
                    )
                    false
                }
            },
            null
        )
        return result
    }


    private fun savePassword(shPref: SharedPreferences, data: Any?) {
        if (data == null) {
            viewModelCommonViewModel.updateItem(
                UserInfo(
                    password = Values.defaultPassword,
                    lastAppState = 0
                )
            )

            //SWITCH
            passwordSwitch?.isChecked = false

            //Alert password is set
            displayAlert(
                R.string.passwordCaption,
                TextO(R.string.passwordIsReset), null, null, null
            )
            return
        }


        displayAlert(
            R.string.passwordCaption,
            TextO(null, "New password: $data Ok?"),
            null,
            {
                //SAVE
                viewModelCommonViewModel.updateItem(UserInfo(password = (data as String), lastAppState = 0))

                //SWITCH
                passwordSwitch?.isChecked = (data as String) != Values.defaultPassword

                //Alert password is set
                displayAlert(
                    R.string.passwordCaption,
                    TextO(R.string.passwordIsSet), null, null, null
                )
            },
            {
                return@displayAlert
            }
        )
    }


    private fun displayAlert(
        caption: Int,
        msg: TextO,
        view: EditText?,
        runOk: ((String?) -> Unit)?,
        runCancel: (() -> Unit)?
    ) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(requireContext().getString(caption))


        dialog.setPositiveButton(android.R.string.ok) { a, b ->
            if (view != null) {
                runOk?.invoke(view.text.toString())
            } else {
                runOk?.invoke(null)
            }
        }

            .setIcon(R.drawable.monitor)

        if (view != null) {
            dialog.setView(view);
        }

        if (runCancel != null) {
            dialog.setNegativeButton(android.R.string.cancel) { a, b ->
                runCancel.invoke()
            }
        }

        msg.rID?.let {
            dialog.setMessage(requireContext().getString(it))
        } ?: dialog.setMessage(msg.text)

        dialog.show()
    }
}

data class TextO(val rID: Int?, val text: String = "")