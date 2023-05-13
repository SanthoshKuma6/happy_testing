package com.edu.happytesting.ui.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.edu.happytesting.ui.manager.DialogMgr
import com.edu.happytesting.utils.showToast

/**
 * Base Activity - This class acts as a common class when extended to an Activity to perform common operations such as throw error, alert users,
 * launch activity etc....
 */
@SuppressLint("InflateParams")
open class BaseActivity : AppCompatActivity(), DialogMgr {

    private var messageSheet: MessageSheet? = null

    /**
     * setStatusBar - to change the status bar color pass the color value to the function
     */


    /**
     * Launches activity with preferred bundle(share data from one screen to another) &
     * isClearPreviousTask (true - clear the backstack and open the activity on top stack ||
     * false - open activity over the current stack without clearing the backstack)
     */

    /**
     * Error response from the api to alert the user
     */
    open val errorObserver = Observer<String> {
        showToast(it)
    }

    /**
     * Shows error message alert to the user.
     */

    override fun onPositiveButtonClicked() {
        messageSheet?.dismiss()
    }
}