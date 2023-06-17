package com.example.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.local.RegisterRequest
import com.example.mystoryapp.viewmodel.AuthenticationViewModel


class RegisterFragment : Fragment(), View.OnClickListener {

    private lateinit var tvRegister: TextView
    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: AuthenticationViewModel

    companion object {
        private val TAG = "RegisterFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        Log.i(TAG, "Object view model di register fragment: ${viewModel}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var flagUsernameNotEmpty = false
        var flagEmailNotEmpty = false
        var flagPasswordValid = false
        tvRegister = view.findViewById(R.id.tv_register)
        edtUsername = view.findViewById(R.id.edt_username)
        edtEmail = view.findViewById(R.id.edt_email)
        edtPassword = view.findViewById(R.id.edt_password)
        btnRegister = view.findViewById(R.id.btn_register)
        progressBar = view.findViewById(R.id.progress_bar)
        btnRegister.setOnClickListener(this)
        btnRegister.isEnabled = false

        edtUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                flagUsernameNotEmpty = s.toString().isNotEmpty()
                btnRegister.isEnabled = flagUsernameNotEmpty && flagEmailNotEmpty && flagPasswordValid
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                flagEmailNotEmpty = s.toString().isNotEmpty()
                btnRegister.isEnabled = flagUsernameNotEmpty && flagEmailNotEmpty && flagPasswordValid
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                flagPasswordValid = s.toString().length >= 8
                btnRegister.isEnabled = flagUsernameNotEmpty && flagEmailNotEmpty && flagPasswordValid
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        playAnimation()

        viewModel.isRegisterLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isRegisterSuccess.observe(viewLifecycleOwner) {
            if (it) {
                afterSuccess()
            }
        }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_register -> {
                    val username = edtUsername.text.trim().toString()
                    val email = edtEmail.text.trim().toString()
                    val password = edtPassword.text.trim().toString()

                    val registerReq = RegisterRequest(username, email, password)
                    viewModel.register(registerReq)
                }
            }
        }
    }

    private fun playAnimation() {
        Log.i(TAG, "play animation dipanggil.")
        val titleTv = ObjectAnimator.ofFloat(tvRegister, View.ALPHA, 1f).setDuration(500)
        val usernameEdt = ObjectAnimator.ofFloat(edtUsername, View.ALPHA, 1f).setDuration(500)
        val emailEdt = ObjectAnimator.ofFloat(edtEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEdt = ObjectAnimator.ofFloat(edtPassword, View.ALPHA, 1f).setDuration(500)
        val registerBtn = ObjectAnimator.ofFloat(btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                titleTv,
                usernameEdt,
                emailEdt,
                passwordEdt,
                registerBtn
            )
            startDelay = 500
        }.start()
    }

    private fun afterSuccess() {
        Log.i(TAG, "after success register")
        // navigate ke login
        val mFragmentManager = parentFragmentManager
        val mLoginFragment = LoginFragment()

        mFragmentManager
            .beginTransaction()
            .replace(R.id.authentication_frame, mLoginFragment, LoginFragment::class.java.simpleName)
            .commit()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}