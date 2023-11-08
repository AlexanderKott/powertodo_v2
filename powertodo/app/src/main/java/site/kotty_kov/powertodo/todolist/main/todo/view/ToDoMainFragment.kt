package site.kotty_kov.powertodo.todolist.main.todo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.FragmentToDoMainBinding
import site.kotty_kov.powertodo.todolist.main.todo.view.pager.SectionsPagerAdapter
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.viewModel.ToDoViewModel


class ToDoMainFragment : Fragment(R.layout.fragment_to_do_main) {

    private lateinit var binding: FragmentToDoMainBinding

    private val viewModelToDoViewModel: ToDoViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentToDoMainBinding.bind(view)

        binding.tabz.setupWithViewPager(binding.viewPager)
        val sectionsPagerAdapter = SectionsPagerAdapter(
            requireContext(),
            getChildFragmentManager()
        )

        binding.viewPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = binding.tabz
        tabLayout.setupWithViewPager(binding.viewPager)

        arguments?.getInt("page")?.let{
            binding.viewPager.currentItem = it
        }


        //перебросить запросы из child фрагментов в активити
        childFragmentManager.setFragmentResultListener(
            Values.drawerMenuKey,
            viewLifecycleOwner
        ) { key, bundle ->
            setFragmentResult(key, bundle)
        }


        //Обработка жестов
        childFragmentManager.setFragmentResultListener(
            Values.switchPagerKey,
            viewLifecycleOwner) { _, bundle ->
            bundle.getString(Values.switchRightKey)?.let {
                var page = binding.viewPager.currentItem
                if (page > 0){
                    binding.viewPager.currentItem = -- page }
            }

            bundle.getString(Values.switchLeftKey)?.let {
                var page = binding.viewPager.currentItem
                if (page < 2)
                    binding.viewPager.currentItem = ++ page
            }

        }


        //получить запись из фрагмента "addEdit" эвенты создания и редактирования записи. callback
        setFragmentResultListener(Values.EditItemDialogFragmentAKey) { _, bundle ->
            //создание
            bundle.getString(Values.newFragmentBundleKey)?.let {
                val trObject: ToDoItemTransfer? =
                    Utils.gsonParser?.fromJson(it, ToDoItemTransfer::class.java)
                if (trObject != null) {
                    viewModelToDoViewModel.addNewItems(trObject)

                }
            }

            //редактирование
            bundle.getString(Values.editFragmentBundleKey)?.let { param ->
                Utils.gsonParser?.fromJson(param, ToDoItemTransfer::class.java)
                    ?.let {
                    viewModelToDoViewModel.updateItem(it)
                }

            }

        }

    }


}