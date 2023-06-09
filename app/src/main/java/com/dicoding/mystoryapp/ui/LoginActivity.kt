package com.dicoding.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.Preference
import com.dicoding.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.mystoryapp.response.LoginResponse
import com.dicoding.mystoryapp.response.LoginResult
import com.dicoding.mystoryapp.viewmodel.AuthViewModel
import com.dicoding.mystoryapp.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var myButton: Button
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val loginViewModel: AuthViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        myButton = findViewById(R.id.btn_login)

        viewModelFactory = ViewModelFactory.getInstance(binding.root.context)

        playAnimation()

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            loginButtonClicked()
        }
    }
    private fun loginButtonClicked() {
        val email = binding.edLoginEmail.text?.trim().toString()
        val pass = binding.edLoginPassword.text?.trim().toString()

        if (binding.edLoginEmail.text.isNullOrEmpty() || binding.edLoginPassword.text.isNullOrEmpty()){
            Toast.makeText(this, getString(R.string.input_first), Toast.LENGTH_SHORT).show()
        }
        if (binding.edLoginEmail.error != null || binding.edLoginPassword.error != null){
            Toast.makeText(this, getString(R.string.input_correctly), Toast.LENGTH_SHORT).show()
        }else{
            loginViewModel.login(email, pass).observe(this){login ->
                if (login != null){
                    when(login){
                        is com.dicoding.mystoryapp.data.Result.Loading -> {
                            showLoading(true)
                        }
                        is com.dicoding.mystoryapp.data.Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                        }
                        is com.dicoding.mystoryapp.data.Result.Success -> {
                            showLoading(false)
                            saveData(login.data)
                            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun saveData(data: LoginResponse) {
        val preference = Preference(this)
        val loginRes = data.loginResult
        val loginResult = LoginResult(userId = loginRes.userId, name = loginRes.name, token = loginRes.token)
        preference.setData(loginResult)
        mainActivity()
    }

    private fun mainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val tagline = ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(login, tagline, email, pass, btn)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}