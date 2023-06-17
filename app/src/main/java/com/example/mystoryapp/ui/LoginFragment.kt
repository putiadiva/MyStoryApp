package com.example.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.local.LoginRequest
import com.example.mystoryapp.viewmodel.AuthenticationViewModel


class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var tvLogin: TextView
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnToRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: AuthenticationViewModel

    companion object {
        private val TAG = "LoginFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        Log.i(TAG, "Object view model di login fragment: ${viewModel}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var flagEmailNotEmpty = false
        var flagPasswordValid = false
        tvLogin = view.findViewById(R.id.tv_login)
        btnLogin = view.findViewById(R.id.btn_login)
        btnToRegister = view.findViewById(R.id.btn_to_register)
        edtEmail = view.findViewById(R.id.edt_email)
        edtPassword = view.findViewById(R.id.edt_password)
        btnLogin.setOnClickListener(this)
        progressBar = view.findViewById(R.id.progress_bar)
        btnToRegister.setOnClickListener(this)
        btnLogin.isEnabled = false

        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                flagEmailNotEmpty = s.toString().isNotEmpty()
                btnLogin.isEnabled = flagPasswordValid && flagEmailNotEmpty
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                flagPasswordValid = s.toString().length >= 8
                btnLogin.isEnabled = flagPasswordValid && flagEmailNotEmpty
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        playAnimation()

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isLoginSuccess.observe(viewLifecycleOwner) {
            if (it) {
                afterSuccess()
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            Log.i(TAG, "view tidak null")
            when (v.id) {
                R.id.btn_login -> {
                    // baca input.
                    val email = edtEmail.text.trim().toString()
                    val password = edtPassword.text.trim().toString()

                    // hit api.
                    val loginReq = LoginRequest(email, password)
                    viewModel.login(loginReq)

                }
                R.id.btn_to_register -> {
                    val mRegisterFragment = RegisterFragment()
                    val mFragmentManager = parentFragmentManager
                    mFragmentManager.beginTransaction().apply {
                        replace(R.id.authentication_frame, mRegisterFragment, RegisterFragment::class.java.simpleName)
                        addToBackStack(null)
                        commit()
                    }
                }
            }
        }
        Log.i(TAG, "view null")
    }

    private fun playAnimation() {
        Log.i(TAG, "play animation dipanggil.")
        val titleTv = ObjectAnimator.ofFloat(tvLogin, View.ALPHA, 1f).setDuration(500)
        val emailEdt = ObjectAnimator.ofFloat(edtEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEdt = ObjectAnimator.ofFloat(edtPassword, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 1f).setDuration(500)
        val toRegisterBtn = ObjectAnimator.ofFloat(btnToRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                titleTv,
                emailEdt,
                passwordEdt,
                loginBtn,
                toRegisterBtn
            )
            startDelay = 500
        }.start()
    }

    private fun afterSuccess() {
        Log.i(TAG, "berhasil login")
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}