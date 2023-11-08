package site.kotty_kov.powertodo.todolist.main.common.login

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import site.kotty_kov.powertodo.R
import site.kotty_kov.powertodo.databinding.FragmentLoginBinding
import site.kotty_kov.powertodo.todolist.main.common.Values
import site.kotty_kov.powertodo.todolist.main.viewModel.CommonViewModel


class LoginFragment : Fragment() {

    private val viewModelViewModel: CommonViewModel by viewModels(
        ownerProducer = { this.requireActivity() })

    private lateinit var binding: FragmentLoginBinding
    private var userEntered = ""
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentLoginBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        viewModelViewModel.getPassword().observe(viewLifecycleOwner, { pass ->
            password = pass
        })

        binding.b0.setOnClickListener {
            addChar("0")
        }
        binding.b1.setOnClickListener {
            addChar("1")
        }
        binding.b2.setOnClickListener {
            addChar("2")
        }
        binding.b3.setOnClickListener {
            addChar("3")
        }
        binding.b4.setOnClickListener {
            addChar("4")
        }
        binding.b5.setOnClickListener {
            addChar("5")
        }
        binding.b6.setOnClickListener {
            addChar("6")
        }
        binding.b7.setOnClickListener {
            addChar("7")
        }
        binding.b8.setOnClickListener {
            addChar("8")
        }

        binding.b9.setOnClickListener {
            addChar("9")
        }

        binding.clear.setOnClickListener {
            val leng = binding.passwordField.text.toString().length
            if (leng > 0) {
                binding.passwordField.setText("")
                userEntered = ""
            }
        }

    }


    fun addChar(tag: String) {
        val char = String(Character.toChars(Values.crownChar))
        val newText = binding.passwordField.text.toString() + char
        val textfield = binding.passwordField
        val constraint = Values.defaultPassword.length

        if (userEntered.length < constraint) {
            textfield.setText(newText)
            userEntered += tag
        } else {
            textfield.setText(char)
            userEntered = tag
        }
        if (userEntered.length == constraint) {
            doPassCheck()
        }

    }

    fun doPassCheck() {
        if (password == userEntered) {
            //unlocked
            setFragmentResult(
                Values.unlock,
                bundleOf(Values.unlock to "true")
            )

        } else {
            userEntered = Values.passwordTemp.take(Values.defaultPassword.length)
            binding.passwordField.setText(R.string.nope)
        }

    }

}


