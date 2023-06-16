package com.edu.happytesting.activity

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.edu.happytesting.R
import com.edu.happytesting.databinding.ActivityMainBinding
import com.edu.happytesting.databinding.DialogeLogoutLayoutBinding
import com.edu.happytesting.preference.HappyPreference
import com.edu.happytesting.utils.MyReceiver


class MainActivity : AppCompatActivity() {

    private val mainActivity by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var myReceiver: MyReceiver



    // use to Deprecation function
//    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
//        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
//        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
//    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainActivity.root)
        myReceiver = MyReceiver()

        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        //get the data from login activity
//        val examData: StudentData.Data = intent.parcelable("user")!!


        mainActivity.logout.setOnClickListener {
logOut()

        }


        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_admin) as NavHostFragment
        navController = navHostFragment.navController
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.top_bar_menu, menu)
//        return true
//    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == R.id.logout) {
//            logout()
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//
//    }




    private fun logOut(){
        val dialog= DialogeLogoutLayoutBinding.inflate(layoutInflater)
        com.edu.happytesting.ui.dialog.AlertDialog().logOut(this,dialog)
        dialog.logOutButton.setOnClickListener {
            HappyPreference(this).saveUserData("", "")
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)

        }


    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }
    @Suppress("DEPRECATION")
    override fun onStart() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(myReceiver, intentFilter)
        super.onStart()

    }

}






