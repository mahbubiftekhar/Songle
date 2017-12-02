package uk.co.iftekhar.www.songle

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_faq.*

class FAQ : AppCompatActivity() {
    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
        * return true, otherwise false*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    override fun onBackPressed() {
        if (isNetworkConnected()) {
            /*If a network connection is avaialble call super.onBackPressed*/
            super.onBackPressed()
        } else {
            /*otherwise, send the user to NetworkIssue splash page */
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        setSupportActionBar(toolbar)
    }
}
