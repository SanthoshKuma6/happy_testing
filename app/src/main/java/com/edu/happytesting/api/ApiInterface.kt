package com.edu.happytesting.api

import com.edu.happytesting.dataclass.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("studentlogin.php")
    suspend fun login(
        @Query("registrationNumber") registrationNumber: String,
        @Query("password") password: String
    ): Response<StudentData>

    @FormUrlEncoded
    @POST("getquestionslist.php")
    suspend fun questionList(@Field("testId") testId: String): Response<List<QuestionList.QuestionListItem>>


    @FormUrlEncoded
    @POST("refreshExamDetails.php")
    suspend fun refreshingQuestionList(
        @Field("classId") classId: String,
        @Field("studentId") studentId: String
    ): Response<List<RefresigExamDetails.RefresigExamDetailsItem>>

    @POST("uploadObjectiveAnswer.php")
//    @POST("")
     suspend fun putObjectiveAnswer(@Body params: JsonObject): Response<ObjectiveAnswer>

    @POST("uploadSubjectiveAnswer.php")
//    @POST("")
     fun putSubjectAnswer(@Body jsonObject: JsonObject): Call<SubjectiveAnswer>

}