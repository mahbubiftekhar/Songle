package uk.co.iftekhar.www.songle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_network_issue.*

class NetworkIssue : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_issue)
        NETWORKISSUEBUTTON.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) /*Start the main activity when button clicked*/
        }
    }
}
