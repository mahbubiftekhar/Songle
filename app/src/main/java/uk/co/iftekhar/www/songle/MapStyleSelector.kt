package uk.co.iftekhar.www.songle

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_mpa_style_selector.*

class MapStyleSelector : AppCompatActivity() {
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
    fun updateHeader(){
        val textView: TextView = findViewById(R.id.currentMAPSTYLE)
        textView.text = "Selected style:  "+ LoadString("MAPSTYLE")
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
