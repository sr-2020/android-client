package org.shadowrunrussia2020.android

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.LoginRequest
import org.shadowrunrussia2020.android.common.models.LoginResponse
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import java.io.IOException

val loginsMap = listOf(
    Pair("t1@foo.bar", "1"),
    Pair("t2@foo.bar", "1"),
    Pair("t3@foo.bar", "1"),
    Pair("t52@foo.bar", "1"),
    Pair("t54@foo.bar", "1")
)

class LoginActivity : AppCompatActivity() {
    private val TAG = "SR2020-LoginActivity"

    private val mApplication by lazy { application as ShadowrunRussia2020Application }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up the login form.
        passwordInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                loginFormData()?.let{
                    attemptLogin(it.email, it.password)
                }
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener {
            loginFormData()?.let {
                attemptLogin(it.email, it.password)
            }
        }

        version.text = "v%s.%d".format(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        loginsMap.forEach { (login, pass) ->
            logins.addView(Button(this).apply {

                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                text = login
                setOnClickListener {
                    attemptLogin(login, pass)
                }
            })
        }

    }

    private fun attemptLogin(email: String, password: String) {

        val service = ApplicationSingletonScope.DependencyProvider.dependency.retrofit.create(AuthenticationWebService::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            showProgress(true)
            try {
                var response = withContext(Dispatchers.IO)
                {
                    service.login(
                        LoginRequest(
                            email = email,
                            password = password,
                            firebase_token = FirebaseInstanceId.getInstance().token!!
                        )
                    )
                }.await()
                saveTokenAndGoToMainActivity(response)
            } catch (e: IOException) {
                passwordInput.error = getString(R.string.error_incorrect_password)
                passwordInput.requestFocus()
            } catch (e: Exception) {
                showErrorMessage(this@LoginActivity, "Сервер недоступен")
            }
            showProgress(false)
        }
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

    private fun saveTokenAndGoToMainActivity(response: LoginResponse) {
        val token = response.api_key
        Log.i(TAG, "Successful login, token = $token")
        ApplicationSingletonScope.DependencyProvider.dependency.session.setTokenAndId(token, response.id)
        startActivity(Intent(this, MainActivity::class.java))
    }
}

