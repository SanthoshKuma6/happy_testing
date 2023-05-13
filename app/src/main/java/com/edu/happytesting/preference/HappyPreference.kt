package com.edu.happytesting.preference

import android.content.Context
import android.content.SharedPreferences
import com.edu.happytesting.R
import com.edu.happytesting.dataclass.StudentData

class HappyPreference(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val STUDENTID="studentId"
        const val CLASSID="class_id"
        const val EXAMDATA="Exam_data"
    }

    /**
     * Function to save user details
     */
    fun saveUserData( studentId: String,classId:String) {
        val editor = prefs.edit()
        editor.putString(STUDENTID, studentId)
        editor.putString(CLASSID,classId)
        editor.apply()
    }

    /**
     * Get the user details
     */
    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        user[STUDENTID] = prefs.getString(STUDENTID, null).toString()
        user[CLASSID] = prefs.getString(CLASSID, null).toString()
        return user
    }

    fun saveExamData(examList: List<StudentData.Data.ExamListItem>) {
        val editor=prefs.edit()
        editor.putString(EXAMDATA,examList.toString())
        editor.apply()

    }


}