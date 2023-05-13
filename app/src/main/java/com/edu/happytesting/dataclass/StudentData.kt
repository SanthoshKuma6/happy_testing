package com.edu.happytesting.dataclass

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentData(

	@field:SerializedName("data")
	val studentData: Data,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("statusCode")
	val statusCode: Int
):Parcelable
{
@Parcelize
	data class Data(

		@field:SerializedName("studentId")
		val studentId: String,

		@field:SerializedName("examList")
		val examList: List<ExamListItem>,

		@field:SerializedName("classId")
		val classId: String,

		@field:SerializedName("name")
		val name: String
	):Parcelable
{

		@Parcelize
		data class ExamListItem(

			@field:SerializedName("duration")
			val duration: Int,

			@field:SerializedName("testStatus")
			val testStatus: String,

			@field:SerializedName("examName")
			val examName: String,

			@field:SerializedName("testId")
			val testId: String,

			@field:SerializedName("numberOfQuestions")
			val numberOfQuestions: Int,

			@field:SerializedName("subjectId")
			val subjectId: String
		):Parcelable
	}



}
