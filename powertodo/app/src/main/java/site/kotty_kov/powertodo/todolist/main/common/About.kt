package site.kotty_kov.powertodo.todolist.main.common

import android.app.AlertDialog
import android.content.Context
import site.kotty_kov.powertodo.R

object About {
    fun showAbout(context : Context){
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.aboutAndLicence))
            .setMessage(context.getString(R.string.aboutText))
            .setPositiveButton(android.R.string.ok) { a, b ->
            }
            .setIcon(android.R.drawable.ic_menu_share)
            .show()

    }
}