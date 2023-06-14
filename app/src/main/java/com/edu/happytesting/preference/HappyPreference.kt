package com.edu.happytesting.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.edu.happytesting.R
import com.edu.happytesting.dataclass.StudentData

class HappyPreference(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val STUDENT_ID="studentId"
        const val CLASS_ID="class_id"
        const val EXAM_DATA="Exam_data"
    }

    /**
     * Function to save user details
     */
    @SuppressLint("SuspiciousIndentation")
    fun saveUserData(studentId: String, classId:String) {
        val editor = prefs.edit()
            editor.putString(STUDENT_ID, studentId)
            editor.putString(CLASS_ID,classId)
            editor.apply()


    }

    /**
     * Get the user details
     */
    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        user[STUDENT_ID] = prefs.getString(STUDENT_ID, null).toString()
        user[CLASS_ID] = prefs.getString(CLASS_ID, null).toString()
        return user
    }

    fun saveExamData(examList: List<StudentData.Data.ExamListItem>) {
        val editor=prefs.edit()
        editor.putString(EXAM_DATA,examList.toString())
        editor.apply()

    }


}