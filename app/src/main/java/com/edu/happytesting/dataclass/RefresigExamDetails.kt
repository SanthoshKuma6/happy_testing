package com.edu.happytesting.dataclass

data class RefresigExamDetails(
	val refresigExamDetails: List<RefresigExamDetailsItem?>? = null
)
{
	data class RefresigExamDetailsItem(
		val duration: Int? = null,
		val testStatus: String? = null,
		val examName: String? = null,
		val testId: String? = null,
		val numberOfQuestions: Int? = null,
		val subjectId: String? = null
	)

}


