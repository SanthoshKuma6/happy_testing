package com.edu.happytesting.repository

import androidx.lifecycle.MutableLiveData
import com.edu.happytesting.ui.HappyClass
import com.edu.happytesting.api.ApiInterface
import com.edu.happytesting.api.Response
import com.edu.happytesting.api.RetrofitApi
import com.edu.happytesting.dataclass.StudentData
import com.edu.happytesting.utils.NetworkUtils
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

class LoginRepository {
    /**
     * NextGenClass.getContext - capture the context from the application class (application class
     * is called during the startup)
     */
    private val context by lazy { HappyClass.getContext }

    /**
     * serverApi - Initialise the retrofit builder to handle the api calls
     */
    private val serverApi by lazy {
        RetrofitApi.retrofitInstance().create(ApiInterface::class.java)
    }
    /**
     * MutableLiveData - Pojo objects are initialised here to add the value from the api response .
     * Since it is MutableLiveData, the added values will be observed in the activity or fragment
     * where the observer is defined.
     */
    val loginLiveData = MutableLiveData<Response<StudentData>>()

    suspend fun loginApi(number: String, password: String) {
        // Check for internet connection availability
        if (NetworkUtils.isInternetAvailable(context = context)) {
            //activate the loader (progress dialog)
            loginLiveData.value = Response.Loading(showLoader = true)
            try {
                //trigger the login api call and get the result
                val result = serverApi.login(number, password)
                if (result.body() != null) {
                    //add the success response to the livedata
                    loginLiveData.value = Response.Success(data = result.body())
                    loginLiveData.value = Response.Loading(showLoader = false)
                } else {
                    //add the error response to the livedata
                    try {
                        loginLiveData.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        loginLiveData.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                //add the error response to the livedata
                loginLiveData.value = Response.Error(errorMessage = e.message.toString())
                loginLiveData.value = Response.Loading(showLoader = false)
            }
        } else {
            //add the error response to the livedata
            loginLiveData.value = Response.Error(errorMessage = "No Internet Connection")
        }
    }


    /**
     * This function returns the message from the errorbody of the responseBody.
     */
    private fun getErrorBodyMessage(responseBody: ResponseBody): String {
        val errorJson = JSONObject(responseBody.string())
        return errorJson.getString("message")
    }
}