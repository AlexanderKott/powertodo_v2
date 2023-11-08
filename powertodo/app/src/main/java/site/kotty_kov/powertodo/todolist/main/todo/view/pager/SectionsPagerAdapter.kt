package site.kotty_kov.powertodo.todolist.main.todo.view.pager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.todolist.main.done.DoneFragment
import site.kotty_kov.powertodo.todolist.main.inprogress.view.InprogressFragment
import site.kotty_kov.powertodo.todolist.main.todo.view.ToDoFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
)


class SectionsPagerAdapter(private val context: Context , fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ToDoFragment()
            1 -> InprogressFragment()
            2 -> DoneFragment()
            else -> throw  UnsupportedClassVersionError()
        }
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }


    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}