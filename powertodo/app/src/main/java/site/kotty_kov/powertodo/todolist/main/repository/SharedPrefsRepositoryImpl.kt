package site.kotty_kov.powertodo.todolist.main.repository

import android.content.Context



class SharedPrefsRepositoryImpl(var context: Context) : SharedPrefsRepository {

    init {
        loadColorButtonsState()
    }

   private  var whiteB = false
   private  var redB = false
   private  var blueB = false
   private  var greenB = false

     private fun loadColorButtonsState() {
        val pref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        whiteB = pref.getBoolean("whiteB", true)
        redB = pref.getBoolean("redB", true)
        blueB = pref.getBoolean("blueB", true)
        greenB = pref.getBoolean("greenB", true)
    }


    override fun saveSettings(whiteB: Boolean, redB: Boolean, blueB: Boolean, greenB: Boolean) {
        val pref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        pref.edit().apply {
            putBoolean("whiteB", whiteB)
            putBoolean("redB", redB)
            putBoolean("blueB", blueB)
            putBoolean("greenB", greenB)
            apply()
        }
    }

    override fun getSelectedColor(): Int {
        return arrayOf(whiteB, redB, blueB, greenB).indexOfFirst { it == true }
    }
        

}