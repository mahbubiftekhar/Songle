package uk.co.iftekhar.www.songle

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_network_issue.*

class NetworkIssue : AppCompatActivity() {
    private fun isNetworkConnected(): Boolean {
          /* function to check if network is available - using isNetworkConnected -
          if network is not available, the user will be taken to the network not available
          splash screen */
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onBackPressed() {
        if (isNetworkConnected()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            /*No data connection, warn user */
            Toast.makeText(this@NetworkIssue, "No data connection, please investigate", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_issue)
        NETWORKISSUEBUTTON.setOnClickListener {
            if (!isNetworkConnected()) {
                Toast.makeText(this@NetworkIssue, "No data connection, please investigate", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent) /*Start the main activity when button clicked*/
            }
        }
        /*The following buttons do NOT require data, hence no further checks will be required*/
        HOWTOPLAYNETWORKISSUE.setOnClickListener {
            val intent = Intent(this, uk.co.iftekhar.www.songle.FAQ::class.java)
            startActivity(intent)
        }
        SCOREBOARD.setOnClickListener {
            val intent = Intent(this, ScoreScreen::class.java)
            startActivity(intent)
        }
        MAPSTYLESELECTOR.setOnClickListener {
            val intent = Intent(this, MapStyleSelector::class.java)
            startActivity(intent)
        }
    }
}
