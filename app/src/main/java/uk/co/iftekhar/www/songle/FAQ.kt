package uk.co.iftekhar.www.songle

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_faq.*

class FAQ : AppCompatActivity() {
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun networkChecker() {
        /*function to check if network is available - using isNetworkConnected -
        if network is not available, the user will be taken to the network not available
        splash screen
         */
        if (!isNetworkConnected()) {
            /*If there is a network issue send uesr to network issue page */
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        networkChecker() /*Check network status*/
        super.onBackPressed()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        setSupportActionBar(toolbar)
    }
}
