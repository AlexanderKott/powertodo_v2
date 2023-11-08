package site.kotty_kov.powertodo.todolist.main.common

import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import site.kotty_kov.powertodo.todolist.main.common.gestures.BottomGestureOnSwipeTouchListener
import site.kotty_kov.powertodo.todolist.main.common.utils.collapseBottom

class GesturesListenerImpl(var fragment: Fragment, var bottom: LinearLayout) :
    BottomGestureOnSwipeTouchListener(fragment) {

    override fun onSwipeRight(): Boolean {
        fragment.setFragmentResult(
            Values.switchPagerKey,
            bundleOf(Values.switchRightKey to "1")
        )
        collapseBottom(bottom)
        return true;
    }

    override fun onSwipeLeft(): Boolean {
        fragment.setFragmentResult(
            Values.switchPagerKey,
            bundleOf(Values.switchLeftKey to "1")
        )
        collapseBottom(bottom)
        return true;
    }
}