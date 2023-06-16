package com.edu.happytesting.ui.dialog

import android.content.Context
import com.edu.happytesting.R
import com.edu.happytesting.databinding.DialogeLogoutLayoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialog {

    fun logOut(context: Context, dialogeLogoutLayoutBinding: DialogeLogoutLayoutBinding) {
        val dialog = MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
        dialog.setView(dialogeLogoutLayoutBinding.root)
        val alert = dialog.create()
        alert.show()
        dialogeLogoutLayoutBinding.cancel.setOnClickListener { alert.dismiss() }

    }

    fun showAlert(context: Context,closeActivity: () -> Unit) {
        val dialog = MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
        dialog.apply {
            setTitle("Alert!")
            setMessage("Are you sure want to exit?")
            setPositiveButton("yes"){_,_ -> closeActivity()}
            setNegativeButton("no"){dialog ,_ ->dialog.dismiss()}
        }
    }
}