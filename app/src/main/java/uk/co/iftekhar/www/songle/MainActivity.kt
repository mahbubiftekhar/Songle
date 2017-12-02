package uk.co.iftekhar.www.songle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert

class MainActivity : AppCompatActivity() {
    var okToContinue = false
    var numberofsongs = 0
    lateinit var SongTitles: Array<String?>
    lateinit var SongLinks: Array<String?>
    var DownloadXMLCompleted = true

    fun SaveInt(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun LoadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getInt(key, 0)
        return savedValue
    }


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

    override fun onBackPressed() {
        /* override the back button, so the user is promted when they wish to leave the app */
        alert("Are you sure you want to exit?") {
            yesButton {
                /*The user wishes to close the app, so be it*/
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(startMain)
            }
            noButton {
                /*If the user changes their minds, thank them for changing their mind */
                Toast.makeText(this@MainActivity, "Glad you changed your mind!", Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun launchXMLDownload() {
        DownloadXMLCompleted = true
        val XMLSONGS = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml" //link for xml file, will not change - if it does im screwed
        val XMLSongs = DownloadXmlTask(this)
        XMLSongs.execute(XMLSONGS)/* Run XML async task */

    }

    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
              * return true, otherwise false*/
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
            //If there is a network issue send uesr to network issue page
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    fun bulkwork(Level: String, Timed: Boolean) {
        /*Function that will be called from the different buttons,
        once passed the level and wheather the user wishes to play
        timed, the the function will launch the MapsActivity with the
        appropriate level and timed values as specified by the user */
        val intent = Intent(this, MapsActivity()::class.java)
        intent.putExtra("Level", Level) /*Pass the level*/
        intent.putExtra("Timed", Timed) /*Pass if timed or not*/
        intent.putExtra("SONGLINKS", SongLinks) /* PASS ALL SONG LINKS*/
        intent.putExtra("SONGTITLES", SongTitles) /*PASS ALL SONG TITLES*/
        intent.putExtra("NUMBEROFSONGS", numberofsongs) /*PASS NUMBER OF SONGS*/
        startActivity(intent)
    }

    fun downloadCompleteXML(result1: List<Entry>) {
        if (result1.isEmpty() || !DownloadXMLCompleted) {
            println("here" + result1.size)
            /*
            This check is here to ensure that if the XML has failed the download - e.g. Maybe due to poor network, I found
            this issue myself if I was walking down stairs and the phone was hopping between different access points, this pop up
            will appear to make the user aware that they.

            This issue particular appears with phones due to phones latching onto
            wifi connections are you walk around campus as they want to reduce 4g data used, even if the wifi connection is of an very
            poor quality. Thus this check should help recover from such an issue.
             */
            alert(" Sorry \n Downloading the songs failed \n Shall we retry?") {
                positiveButton("Yes please!") {
                    networkChecker()
                    launchXMLDownload()
                }
                negativeButton("No Thanks!") {
                    networkChecker()
                }
            }.show()
        } else {
            /*This part should execute by the call back from OnPostExecute iff the download was a success */
            val numberofsongs2 = (result1.size) /*Number of songs in XML, please note 0 is the start */
            println("ASYNC IN MAIN: " + numberofsongs2)
            val SongTitles2 = arrayOfNulls<String>(numberofsongs2 + 1)
            val SongLinks2 = arrayOfNulls<String>(numberofsongs2 + 1)
            /* Populate SongTitles and SongLinks */
            for (i in 0..numberofsongs2 - 1) {
                val a = result1[i].link
                SongLinks2[i] = a
            }
            SongTitles2[0] = "GUESS THE SONG!!"
            for (i in 1..numberofsongs2) {
                val b = result1[i - 1].title
                SongTitles2[i] = b
            }
            numberofsongs = numberofsongs2
            SongTitles = SongTitles2
            SongLinks = SongLinks2
            okToContinue = true

            val a = LoadInt("NUMSONGS") /*Load the previous number of songs, this will be 0 on the first opening of the app*/
            if (a < numberofsongs) {
                /* tell the user if new songs have been added */
                SaveInt("NUMSONGS", numberofsongs)
                if (a != 0) {
                    /* This check is to ensure that one the first load of the app this toast doesn't execute
                    * as it will be a bit odd to tell the user they have new songs on there first go*/
                    Toast.makeText(this@MainActivity, "New songs found!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        networkChecker() /* check for data connection */
        okToContinue = false
        launchXMLDownload()
        LEVEL1_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            if (okToContinue && isNetworkConnected() && numberofsongs > 0) {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkwork("5", true) /*Easiest, most words with lots of classifications */
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("5", false) /*Easiest, most words with lots of classifications */
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@MainActivity, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            } else {
                if (!DownloadXMLCompleted || numberofsongs == 0) {
                    launchXMLDownload()
                    Toast.makeText(this@MainActivity, "Downloading songs, please retry in a bit", Toast.LENGTH_SHORT).show()
                }
            }
        }
        SCORE.setOnClickListener {
            val intent = Intent(this, ScoreScreen::class.java)
            startActivity(intent)
        }
        LEVEL2_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            if (isNetworkConnected() && numberofsongs > 0) {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkwork("4", true)
                        //timed play
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("4", false)
                        //No timed play
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@MainActivity, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            } else {
                if (!DownloadXMLCompleted || numberofsongs == 0) {
                    launchXMLDownload()
                    Toast.makeText(this@MainActivity, "Downloading songs, please retry in a bit", Toast.LENGTH_SHORT).show()
                }
            }
        }

        LEVEL3_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            if (okToContinue && isNetworkConnected() && numberofsongs > 0) {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkwork("3", true)
                        //timed play
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("3", false)
                        //No timed play
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@MainActivity, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            } else {
                if (!DownloadXMLCompleted || numberofsongs == 0) {
                    launchXMLDownload()
                    Toast.makeText(this@MainActivity, "Downloading songs, please retry in a bit", Toast.LENGTH_SHORT).show()
                }
            }

        }
        LEVEL4_BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            if (okToContinue && isNetworkConnected() && numberofsongs > 0) {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkwork("2", true)
                        //timed play
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("2", false)
                        //No timed play
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@MainActivity, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            } else {
                if (!DownloadXMLCompleted || numberofsongs == 0) {
                    launchXMLDownload()
                    Toast.makeText(this@MainActivity, "Downloading songs, please retry in a bit", Toast.LENGTH_SHORT).show()
                }
            }
        }
        LEVEL5BUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            if (okToContinue && isNetworkConnected() && numberofsongs > 0) {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkwork("1", true)
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("1", false)
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@MainActivity, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }

                }.show()
            } else {
                if (!DownloadXMLCompleted || numberofsongs == 0) {
                    launchXMLDownload()
                    Toast.makeText(this@MainActivity, "Downloading songs, please retry in a bit", Toast.LENGTH_SHORT).show()
                }
            }
        }
        MUSICBUTTON.setOnClickListener {
            networkChecker() /*Run the network checker */
            if (okToContinue && isNetworkConnected() && numberofsongs > 0) {
                val intent = Intent(this, MusicActivity()::class.java)
                intent.putExtra("SONGLINKS", SongLinks) /* PASS ALL SONG LINKS*/
                intent.putExtra("SONGTITLES", SongTitles) /*PASS ALL SONG TITLES*/
                intent.putExtra("NUMBEROFSONGS", numberofsongs) /*PASS NUMBER OF SONGS*/
                startActivity(intent)
            } else {
                if (!DownloadXMLCompleted || numberofsongs == 0) {
                    launchXMLDownload()
                    Toast.makeText(this@MainActivity, "Downloading songs, please retry in a bit", Toast.LENGTH_SHORT).show()
                }
            }
        }
        MAPSTYLE.setOnClickListener {
            /*Change to MAPSTYLE activity, no checks requried as this activity
            * will work offiline*/
            val intent = Intent(this, MapStyleSelector()::class.java)
            startActivity(intent)
        }
        SONGLYRICS.setOnClickListener {
            networkChecker() /*Run the network checker */
            if (okToContinue && isNetworkConnected() && numberofsongs > 0) {
                val intent = Intent(this, SongLyricActivity()::class.java)
                intent.putExtra("SONGLINKS", SongLinks) /* PASS ALL SONG LINKS*/
                intent.putExtra("SONGTITLES", SongTitles) /*PASS ALL SONG TITLES*/
                intent.putExtra("NUMBEROFSONGS", numberofsongs) /*PASS NUMBER OF SONGS*/
                startActivity(intent)
            } else {
                if (!DownloadXMLCompleted || numberofsongs == 0) {
                    launchXMLDownload()
                    Toast.makeText(this@MainActivity, "Downloading songs, please retry in a bit", Toast.LENGTH_SHORT).show()
                }
            }
        }
        HOWTOPLAY.setOnClickListener {
            /*Change to FAQ activity, no checks requried as this activity
            * will work offiline*/
            val intent = Intent(this, FAQ::class.java)
            startActivity(intent)
        }
    }
}