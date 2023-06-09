package com.edu.happytesting.dataclass

data class RefreshingExamDetails(
	val refresigExamDetails: List<RefreshingExamDetailsItem?>? = null
)
{
	data class RefreshingExamDetailsItem(
		val duration: Int? = null,
		val testStatus: String? = null,
		val examName: String? = null,
		val testId: String? = null,
		val numberOfQuestions: Int? = null,
		val subjectId: String? = null
	)

}


