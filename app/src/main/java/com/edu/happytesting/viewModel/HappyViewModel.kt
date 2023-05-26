package com.edu.happytesting.viewModel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edu.happytesting.repository.HappyRepository
import com.edu.happytesting.repository.LoginRepository
import com.google.gson.JsonObject

class HappyViewModel : ViewModel() {
    /**
     * ViewModel - The ViewModel class is a business logic or screen level state holder.
     * It exposes state to the UI and encapsulates related business logic.
     */

    //repository contains the api call functions
    private val loginRepository by lazy { LoginRepository() }

    //initialize the live data in viewmodel and will be accessed from activity
    val loginResponseLiveData by lazy { loginRepository.loginLiveData }

    //error handler liveData
    val errorHandlerLiveData: MutableLiveData<String> = MutableLiveData()

    /**
     * validate the parameters need to make the login server calls.
     */

    suspend fun login(emailAddress: String, password: String) {
        when {
            TextUtils.isEmpty(emailAddress) -> errorHandlerLiveData.value =
                "Username can't be empty"
//            !isEmailValid(emailAddress) -> errorHandlerLiveData.value = "Enter a valid email address"
            TextUtils.isEmpty(password) -> errorHandlerLiveData.value = "Password can't be empty"
            else -> {
                JsonObject().apply {
                    addProperty("registrationNumber", emailAddress)
                    addProperty("password", password)
                }
                //send the parameters to the repository
                loginRepository.loginApi(emailAddress, password)
            }
        }
    }

    private val happyRepository by lazy { HappyRepository() }
    val questionListResponse by lazy { happyRepository.mutableLiveData }

    suspend fun getQuestionList(testId: String) {
        JsonObject().apply {
            addProperty("testId", testId)
        }
        happyRepository.questionList(testId)
    }

    val refreshingListData by lazy { happyRepository.refreshingData }
    suspend fun getRefreshingData(classId: String, studentId: String) {
        JsonObject().apply {
            addProperty("classId", "studentId")
        }
        happyRepository.getRefreshingData(classId, studentId)
    }
    val objectivesAnswer by lazy { happyRepository.objectiveAnswer }
     suspend fun putObjectiveAnswer(jsonObject: JsonObject) {
        happyRepository.putObjectiveAnswer(jsonObject)
    }
    val subjectiveAnswer by lazy { happyRepository.subjectAnswer }
       fun putSubjectiveAnswer(jsonObject: JsonObject) {
        happyRepository.putSubjectAnswers(jsonObject)
    }

}