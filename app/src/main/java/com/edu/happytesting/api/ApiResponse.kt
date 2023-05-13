package com.edu.happytesting.api

/**
 * Response sealed classes contains subclasses such as Loading, Success, Error
 * are the common api response states used commonly to handle the error or success
 * response in the UI
 */
sealed class Response<T>(
    val data: T? = null,
    val errorMessage: String? = null,
    val showLoader: Boolean? = null
)
{
    class Success<T>(data: T? = null) : Response<T>(data = data)
    class Error<T>(errorMessage: String) : Response<T>(errorMessage = errorMessage)
    class Loading<T>(showLoader: Boolean) : Response<T>(showLoader = showLoader)

}


