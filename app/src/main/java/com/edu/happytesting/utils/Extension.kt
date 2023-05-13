package com.edu.happytesting.utils

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.edu.happytesting.R


/**
 * Extension kotlin class is used to declare function that are commonly used through out the project.
 */

//To check the email address is valid or not
//private val EMAIL_REGEX = "[A-Z0-9a-z.-_]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,3}"
//fun isEmailValid(email: String): Boolean = EMAIL_REGEX.toRegex().matches(email)

//returns the toast message
fun Context.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//returns the log
fun showLog(message: String) = Log.d("nextgentech_log", message)

//used to hide the password visible toggle.
fun showHidePassword(editText: EditText, imageButton: ImageButton) {
    try {
        if (editText.transformationMethod.equals(
                PasswordTransformationMethod.getInstance()
            )
        ) {
            imageButton.setImageResource(R.drawable.hide_password)

            //Show Password
            editText.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

        } else {
            imageButton.setImageResource(R.drawable.view_password)

            //Hide Password
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance()

        }
        editText.setSelection(editText.text.toString().length)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}
