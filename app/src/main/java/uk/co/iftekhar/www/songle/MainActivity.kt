package uk.co.iftekhar.www.songle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.net.ConnectivityManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert

class MainActivity : AppCompatActivity() {
    var networkPref = "any" /* Have preset network communication as any */

    private inner class NetworkReceiver : BroadcastReceiver() { /*network receiver - from slidse */
        override fun onReceive(context: Context, intent: Intent) {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkPref == "wifi" && networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
                // Wi´Fi is connected, so use Wi´Fi
            } else if (networkPref == "any" && networkInfo != null) {
                // Have a network connection and permission, so use data
            } else {
                // No Wi´Fi and no permission, or no network connection
            }
        }
    }

    override fun onBackPressed() { // override the back button, so the user is promted when they wish to leave the app
        alert("Are you sure you want to exit?") {
            yesButton {
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(startMain)
            }
            noButton {
                Toast.makeText(this@MainActivity, "Glad you changed your mind!", Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        return networkInfo != null && networkInfo.isConnected // 3
    }

    fun networkChecker() {
        /*function to check if network is available - using isNetworkConnected -
        if network is not available, the user will be taken to the network not available
        splash screen
         */
        if (!isNetworkConnected()) {
            //If there is a network issue send uesr to network issue page
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    fun bulkwork(Level: String, Timed: Boolean) {
        /*
        Function that will be called from the different buttons,
        once passed the level and wheather the user wishes to play
        timed, the the function will launch the MapsActivity with the
        appropriate level and timed values as specified by the user
         */
        val intent = Intent(this, MapsActivity()::class.java)
        intent.putExtra("Level", Level) /*Pass the level*/
        intent.putExtra("Timed", Timed) /*Pass if timed or not*/
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LEVEL1_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            alert("Want a challenge with timed play?") {
                positiveButton("Yes, bring it on!") {
                    bulkwork("5", true) /*Easiest, most words with lots of classificatins */
                }
                negativeButton("No Thanks!") {
                    bulkwork("5", false) /*Easiest, most words with lots of classificatins */
                }
            }.show()
        }
        LEVEL2_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            alert("Want a challenge with timed play?") {
                positiveButton("Yes, bring it on!") {
                    bulkwork("4", true)
                    //timed play
                }
                negativeButton("No Thanks!") {
                    bulkwork("4", false)
                    //No timed play
                }
            }.show()
        }

        LEVEL3_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            alert("Want a challenge with timed play?") {
                positiveButton("Yes, bring it on!") {
                    bulkwork("3", true)
                    //timed play
                }
                negativeButton("No Thanks!") {
                    bulkwork("3", false)
                    //No timed play
                }
            }.show()

        }
        LEVEL4_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            alert("Want a challenge with timed play?") {
                positiveButton("Yes, bring it on!") {
                    bulkwork("2", true)
                    //timed play
                }
                negativeButton("No Thanks!") {
                    bulkwork("2", false)
                    //No timed play
                }
            }.show()
        }
        LEVEL5BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            alert("Want a challenge with timed play?") {
                positiveButton("Yes, bring it on!") {
                    bulkwork("1", true)
                    //timed play
                }
                negativeButton("No Thanks!") {
                    bulkwork("1", false)
                    //No timed play
                }
            }.show()
            /*Hardest, least words with no classificatins */
        }
        MUSICBUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            val intent = Intent(this, MusicActivity()::class.java)
            startActivity(intent)
        }

        SONGLYRICS.setOnClickListener {
            networkChecker() /*Run the network checker */
            val intent = Intent(this, SongLyricActivity()::class.java)
            startActivity(intent)

        }

        HOWTOPLAY.setOnClickListener {
            val intent = Intent(this, FAQ::class.java)
            startActivity(intent)
        }
    }
}