package org.shadowrunrussia2020.android

import org.shadowrunrussia2020.android.models.LoginRequest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    private val TAG = "SR2020-LoginActivity"

    private val mApplication by lazy { application as ShadowrunRussia2020Application }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up the login form.
        passwordInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }

        settingsButton.setOnClickListener { showSettings() }

        version.text = "v%s.%d".format(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

    private fun attemptLogin() {
        val loginFormData = loginFormData() ?: return
        val service = (application as ShadowrunRussia2020Application)
            .getRetrofit().create(AuthenticationWebService::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            showProgress(true)
            try {
                var response = withContext(Dispatchers.IO)
                { service.login(LoginRequest(loginFormData.email, loginFormData.password)) }.await()
                saveTokenAndGoToMainActivity(response.api_key)
            } catch (e: IOException) {
                passwordInput.error = getString(R.string.error_incorrect_password)
                passwordInput.requestFocus()
            } catch (e: Exception) {
                val toast = Toast.makeText(this@LoginActivity, "Сервер недоступен", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }
            showProgress(false)
        }
    }

    private fun showSettings() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_server_address_title))

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(getBackendUrl(application, this))
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.ok)) { _, _ -> saveServerAddress(input.text.toString()) }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    @SuppressLint("ApplySharedPref")
    private fun saveServerAddress(address: String) {
        mApplication.getGlobalSharedPreferences()
            .edit()
            .putString(getString(R.string.backend_url_key), address)
            .commit()
    }

    private inner class LoginFormData(var email: String, var password: String)

    private fun loginFormData(): LoginFormData? {
        emailInput.error = null
        passwordInput.error = null
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordInput.error = getString(R.string.error_empty_password)
            passwordInput.requestFocus()
            return null
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailInput.error = getString(R.string.error_field_required)
            emailInput.requestFocus()
            return null
        }

        return LoginFormData(email, password)
    }

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate().setDuration(shortAnimTime.toLong()).alpha(
            (if (show) 0 else 1).toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                login_form.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate().setDuration(shortAnimTime.toLong()).alpha(
            (if (show) 1 else 0).toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                login_progress.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    private fun saveTokenAndGoToMainActivity(token: String) {
        Log.i(TAG, "Successful login, token = $token")
        mApplication.getSession().setToken(token)
        startActivity(Intent(this, MainActivity::class.java))
    }
}

