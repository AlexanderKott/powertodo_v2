package site.kotty_kov.powertodo.todolist.main.common.utils

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.ClipboardManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import site.kotty_kov.powertodo.todolist.main.common.Values

object ColorUtils {
    fun parse(color: Int): Int {
        return Color.parseColor(Values.colours[color])
    }
}


fun setClipboard(context: Context, text: String) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = text
    } else {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }
}


fun View.hideKeyboard() {
    val inputMethodManager =
         context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun collapseBottom(bottom: LinearLayout) {
    BottomSheetBehavior.from(bottom as View).state = BottomSheetBehavior.STATE_COLLAPSED
}