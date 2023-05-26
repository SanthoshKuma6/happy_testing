package com.edu.happytesting.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.edu.happytesting.api.ApiInterface
import com.edu.happytesting.api.Response
import com.edu.happytesting.api.RetrofitApi
import com.edu.happytesting.dataclass.*
import com.edu.happytesting.ui.HappyClass
import com.edu.happytesting.utils.NetworkUtils
import com.google.gson.JsonObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class HappyRepository {
    private val context by lazy { HappyClass.getContext }
    private val mainScope by lazy { MainScope() }
    private val serverApi by lazy {
        RetrofitApi.retrofitInstance().create(ApiInterface::class.java)
    }

    private fun getErrorBodyMessage(responseBody: ResponseBody): String {
        val errorJson = JSONObject(responseBody.string())
        return errorJson.getString("message")
    }

    val mutableLiveData = MutableLiveData<Response<List<QuestionList.QuestionListItem>>>()

    suspend fun questionList(testId: String) {
        if (NetworkUtils.isInternetAvailable(context = context)) {
            mutableLiveData.value = Response.Loading(showLoader = true)
            try {
                val result = serverApi.questionList(testId)
                if (result.body() != null) {
                    mutableLiveData.value = Response.Success(data = result.body())
                    mutableLiveData.value = Response.Loading(showLoader = false)
                } else {
                    mutableLiveData.value =
                        Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    mutableLiveData.value = Response.Error(errorMessage = result.message())
                    try {
                        mutableLiveData.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        mutableLiveData.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                mutableLiveData.value = Response.Error(errorMessage = e.message.toString())
                mutableLiveData.value = Response.Loading(showLoader = false)
            }
        } else {
            mutableLiveData.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }

    val refreshingData = MutableLiveData<Response<List<RefresigExamDetails.RefresigExamDetailsItem>>>()

    suspend fun getRefreshingData(classId: String, studentId: String) {
        if (NetworkUtils.isInternetAvailable(context = context)){
        refreshingData.value=Response.Loading(showLoader = true)
            try {
                val result = serverApi.refreshingQuestionList(classId, studentId)
                if (result.body() != null) {
                    refreshingData.value = Response.Success(data = result.body())
                    refreshingData.value = Response.Loading(showLoader = false)
                } else {
                    try {
                        refreshingData.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        refreshingData.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }

            } catch (e: Exception) {
                refreshingData.value = Response.Error(errorMessage = e.message.toString())
                refreshingData.value = Response.Loading(showLoader = false)
            }
    }
        else {
            refreshingData.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }


    val objectiveAnswer = MutableLiveData<Response<ObjectiveAnswer>>()
      suspend fun putObjectiveAnswer(jsonObject: JsonObject) {
        if (NetworkUtils.isInternetAvailable(context = context)) {

            try {
//                val result = serverApi.putObjectiveAnswer(jsonObject = jsonObject)
                val result = serverApi.putObjectiveAnswer(jsonObject)
                if (result.body() != null) {
                    objectiveAnswer.value = Response.Success(data = result.body())
                    objectiveAnswer.value = Response.Loading(showLoader = false)

                } else {
                    try {
                        objectiveAnswer.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    }catch (e:JSONException){
                        objectiveAnswer.value=Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }

                }

            } catch (e: Exception) {
                objectiveAnswer.value = Response.Error(errorMessage = e.message.toString())
                objectiveAnswer.value = Response.Loading(showLoader = false)
            }


//            mainScope.launch {
//                serverApi.putObjectiveAnswer(jsonObject = jsonObject).enqueue(object:
//                    Callback<ObjectiveAnswer>{
//                    override fun onResponse(
//                        call: Call<ObjectiveAnswer>,
//                        response: retrofit2.Response<ObjectiveAnswer>
//                    ) {
//                        val data = response.body()
//                        if (data?.statusCode == 200) {
//                            objectiveAnswer.value = Response.Success(data)
//                            objectiveAnswer.value = Response.Loading(showLoader = false)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<ObjectiveAnswer>, t: Throwable) {
//                        objectiveAnswer.value=Response.Error(t.message.toString())
//
//                    }
//
//                })
//            }


        }else{
            objectiveAnswer.value=Response.Error(errorMessage = "No Internet Connection")
        }
    }

    val subjectAnswer = MutableLiveData<Response<SubjectiveAnswer>>()
       fun putSubjectAnswers(jsonObject: JsonObject) {

        if (NetworkUtils.isInternetAvailable(context = context)) {

//            try {
//                val result = serverApi.putSubjectAnswer(jsonObject)
//                if (result.body() != null) {
//                    subjectAnswer.value = Response.Success(data = result.body())
//                    subjectAnswer.value = Response.Loading(showLoader = false)
//
//                } else {
//                    try {
//                        subjectAnswer.value =
//                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
//                    }catch (e:JSONException){
//                        subjectAnswer.value=Response.Error(errorMessage = result.message())
//                        e.printStackTrace()
//                    }
//                }
//            }
//            catch (e: Exception) {
//                subjectAnswer.value = Response.Error(errorMessage = e.message.toString())
//                subjectAnswer.value = Response.Loading(showLoader = false)
//            }

            mainScope.launch {
                serverApi.putSubjectAnswer(jsonObject=jsonObject).enqueue(object :
                    Callback<SubjectiveAnswer>{
                    override fun onResponse(
                        call: Call<SubjectiveAnswer>,
                        response: retrofit2.Response<SubjectiveAnswer>
                    )
                    {
                        val data = response.body()
                        if (data?.statusCode == 200) {
                            Log.d("onResponseTAG", "onResponse: ${response.body()}")
                            subjectAnswer.value = Response.Success(data)
                            subjectAnswer.value = Response.Loading(showLoader = false)
                        }
                    }

                    override fun onFailure(call: Call<SubjectiveAnswer>, t: Throwable) {
                        subjectAnswer.value=Response.Error(t.message.toString())
                        Log.d("onFailureTAG", "onFailure: ${t.message}")
                    }

                })
            }


        } else {
            subjectAnswer.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }

}