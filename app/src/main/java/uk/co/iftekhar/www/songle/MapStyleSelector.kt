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
        /*Function to update a string sharedPreference*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun LoadString(key: String): String {
        /*Function to load a string sharedPreference*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getString(key, "STANDARD")
        return savedValue
    }

    @SuppressLint("SetTextI18n")
    fun updateHeader() {
        /*Update selected style text on screen*/
        val textView: TextView = findViewById(R.id.currentMAPSTYLE)
        val UserSelectedMapStyle = LoadString("MAPSTYLE")
        textView.text = "Selected style: $UserSelectedMapStyle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpa_style_selector)
        updateHeader()
        STYLE1.setOnClickListener {
            SaveString("MAPSTYLE", "STANDARD") /* update the MAPSTYLE TO STANDARD*/
            updateHeader() /*Update the text saying which map style is selected*/
        }
        STYLE2.setOnClickListener {
            SaveString("MAPSTYLE", "SILVER")/* update the MAPSTYLE TO SILVER*/
            updateHeader() /*Update the text saying which map style is selected*/

        }
        STYLE3.setOnClickListener {
            SaveString("MAPSTYLE", "RETRO")/* update the MAPSTYLE TO RETRO*/
            updateHeader() /*Update the text saying which map style is selected*/
        }
        STYLE4.setOnClickListener {
            SaveString("MAPSTYLE", "DARK")/* update the MAPSTYLE TO DARK*/
            updateHeader() /*Update the text saying which map style is selected*/
        }
        STYLE5.setOnClickListener {
            SaveString("MAPSTYLE", "NIGHT")/* update the MAPSTYLE TO NIGHT*/
            updateHeader() /*Update the text saying which map style is selected*/
        }
        STYLE6.setOnClickListener {
            SaveString("MAPSTYLE", "AUBERGINE")/* update the MAPSTYLE TO AUBERGINE*/
            updateHeader() /*Update the text saying which map style is selected*/
        }
    }
}
