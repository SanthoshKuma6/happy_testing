package com.edu.happytesting.utils

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.edu.happytesting.R

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (!NetworkUtils.isInternetAvailable(context!!)) {
                val alertDialog = AlertDialog.Builder(context).create()
                val layout: View = LayoutInflater.from(context).inflate(R.layout.layout_no_internet, null)
                val retry = layout.findViewById<Button>(R.id.retry)
                alertDialog.setView(layout)
                alertDialog.setCancelable(false)
                alertDialog.show()
                retry.setOnClickListener {
                    alertDialog.dismiss()
                    onReceive(context, intent)
                }
            }

        }catch (e:RuntimeException){
            e.printStackTrace()
        }
    }

class NetworkUtils {

    companion object {
        fun isInternetAvailable(context: Context): Boolean {
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
                return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) ?: false
            }
        }
    }
}


}