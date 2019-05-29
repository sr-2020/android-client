package org.shadowrunrussia2020.android

import `in`.aerem.comconbeacons.models.LoginRequest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
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
import org.shadowrunrussia2020.android.models.LoginResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    private val TAG = "SR2020-LoginActivity"

    // Keep track of the login task to ensure we can cancel it if requested.
    private var mAuthTask: UserLoginTask? = null

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
        if (mAuthTask != null) {
            return
        }
        val loginFormData = loginFormData()
        if (loginFormData != null) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(loginFormData.email, loginFormData.password)
            mAuthTask!!.execute(null as Void?)
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
        goToMainActivity()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    inner class UserLoginTask internal constructor(email: String, password: String) : AsyncTask<Void, Void, LoginResult>() {
        private val mLoginRequest: LoginRequest = LoginRequest(email, password)

        override fun doInBackground(vararg voids: Void): LoginResult {
            try {
                val service = Retrofit.Builder()
                    .baseUrl(getBackendUrl(application, this@LoginActivity))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(AuthenticationWebService::class.java)
                val c = service.login(mLoginRequest)
                val response = c.execute()
                if (response.isSuccessful) {
                    return LoginResult(true, false, response.body()!!.api_key)
                }
                Log.e(TAG, "Unsuccessful response: " + response.errorBody())
                return LoginResult(false, false, "")
            } catch (e: Exception) {
                Log.e(TAG, "IOException: $e")
                return LoginResult(false, true, "")
            }
        }

        override fun onPostExecute(result: LoginResult) {
            onFinish()
            if (result.success) {
                saveTokenAndGoToMainActivity(result.apiKey)
            } else if (result.noConnection) {
                val toast = Toast.makeText(this@LoginActivity, "Сервер недоступен", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            } else {
                passwordInput.error = getString(R.string.error_incorrect_password)
                passwordInput.requestFocus()
            }
        }

        override fun onCancelled() {
            onFinish()
        }

        private fun onFinish() {
            mAuthTask = null
            showProgress(false)
        }
    }
}

