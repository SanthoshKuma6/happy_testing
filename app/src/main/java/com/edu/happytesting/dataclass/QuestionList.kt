package com.edu.happytesting.dataclass

import com.google.gson.annotations.SerializedName

class QuestionList : ArrayList<QuestionList.QuestionListItem>() {
    data class QuestionListItem(
        @SerializedName("id")
        val id: String,
        @SerializedName("mark")
        val mark: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("options")
        val options: List<String?>? = null,
        @SerializedName("type")
        var type: Int? = 0,
        val isAnswered: Boolean=false,
        val chosenAnswer: String? = null,
        val encodedAnswer: String? = null

    )
}

