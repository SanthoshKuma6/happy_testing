package com.edu.happytesting.activity

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Paint
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.edu.happytesting.R
import com.edu.happytesting.api.Response
import com.edu.happytesting.databinding.ActivityLoginBinding
import com.edu.happytesting.dataclass.StudentData
import com.edu.happytesting.preference.HappyPreference
import com.edu.happytesting.ui.base.BaseActivity
import com.edu.happytesting.utils.MyReceiver
import com.edu.happytesting.utils.showHidePassword
import com.edu.happytesting.utils.showToast
import com.edu.happytesting.viewModel.HappyViewModel
import kotlinx.coroutines.launch


class LoginActivity : BaseActivity(),OnClickListener {
    private val loginActivity by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val happyViewModel: HappyViewModel by viewModels()
    private lateinit var myReceiver: MyReceiver

    companion object {
        val studentexamdata: ArrayList<StudentData.Data.ExamListItem> = ArrayList()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(loginActivity.root)
        loginActivity.submit.setOnClickListener {
            validateLogin()
        }
        myReceiver= MyReceiver()
        loginActivity.username.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
//        loginActivity.password.inputType=InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        loginActivity.submit.setOnClickListener(this)
        loginActivity.passwordToggle.setOnClickListener(this)
        happyViewModel.loginResponseLiveData.observe(this, loginObserver)
        happyViewModel.errorHandlerLiveData.observe(this, errorObserver)
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private val loginObserver = Observer<Response<StudentData>> {
        when (it) {
            is Response.Success -> {
                if (it.data?.studentData != null) {
                    it.data.studentData
                    val studentId = it.data.studentData
                    val classId = it.data.studentData
                    //save to companion object
                    studentexamdata.addAll(it.data.studentData.examList)
                    //save to shared preference
                    HappyPreference(context = this).saveUserData(
                         studentId.studentId, classId.classId,
                    )
//                if (it.data.studentData.classId == "1") {
                    //passing data to main activity
                    val user = it.data
                    val i = Intent(this, MainActivity::class.java)
                    i.putExtra("user", user)
                    //store the data to local
                    HappyPreference(context = this).saveExamData(user.studentData.examList)
                    startActivity(i)
                } else {
                    showToast("" + it.data?.message)
                }
            }
            is Response.Error -> {
                showToast("" + it.errorMessage)
            }
            is Response.Loading -> {


            }
        }
    }
    private fun validateLogin() {
        proceedLogin(
            emailAddress = loginActivity.username.text.toString(),
            password = loginActivity.password.text.toString()
        )

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            loginActivity.submit.id -> validateLogin()

            loginActivity.passwordToggle.id -> showHidePassword(
                editText = loginActivity.password,
                imageButton = loginActivity.passwordToggle
            )


        }
    }

    private fun proceedLogin(emailAddress: String, password: String) {
        lifecycleScope.launch {
            happyViewModel.login(
                emailAddress = emailAddress, password = password
            )

        }

    }
    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onStart() {
        val intent=IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(myReceiver,intent)
        super.onStart()
    }






}
