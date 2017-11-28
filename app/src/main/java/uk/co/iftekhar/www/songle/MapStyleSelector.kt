package uk.co.iftekhar.www.songle

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_mpa_style_selector.*

class MapStyleSelector : AppCompatActivity() {
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
            /*If there is a network issue send user to network issue page */
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        networkChecker()
        super.onBackPressed()
    }

    fun SaveString(key: String, value: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun LoadString(key: String): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getString(key, "STANDARD")
        return savedValue
    }

    @SuppressLint("SetTextI18n")
    fun updateHeader() {
        val textView: TextView = findViewById(R.id.currentMAPSTYLE)
        textView.text = "Selected style:  " + LoadString("MAPSTYLE")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpa_style_selector)
        updateHeader()

        STYLE1.setOnClickListener {
            SaveString("MAPSTYLE", "STANDARD")
            updateHeader()
        }
        STYLE2.setOnClickListener {
            SaveString("MAPSTYLE", "SILVER")
            updateHeader()

        }
        STYLE3.setOnClickListener {
            SaveString("MAPSTYLE", "RETRO")
            updateHeader()
        }
        STYLE4.setOnClickListener {
            SaveString("MAPSTYLE", "DARK")
            updateHeader()
        }
        STYLE5.setOnClickListener {
            SaveString("MAPSTYLE", "NIGHT")
            updateHeader()
        }
        STYLE6.setOnClickListener {
            SaveString("MAPSTYLE", "AUBERGINE")
            updateHeader()
        }


    }
}
