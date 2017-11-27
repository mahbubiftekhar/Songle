package uk.co.iftekhar.www.songle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_network_issue.*

class NetworkIssue : AppCompatActivity() {
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_issue)
        NETWORKISSUEBUTTON.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) /*Start the main activity when button clicked*/
        }
        HOWTOPLAYNETWORKISSUE.setOnClickListener {
            val intent = Intent(this, uk.co.iftekhar.www.songle.FAQ::class.java)
            startActivity(intent)
        }
        SCOREBOARD.setOnClickListener {
            val intent = Intent(this, ScoreScreen::class.java)
            startActivity(intent)
        }
    }
}
