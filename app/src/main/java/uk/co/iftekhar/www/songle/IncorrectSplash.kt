package uk.co.iftekhar.www.songle

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.jetbrains.anko.alert
import kotlinx.android.synthetic.main.activity_incorrect_splash.*


class IncorrectSplash : AppCompatActivity() {
    var numberofsongs = 0
    lateinit var SongTitles: Array<String?>
    lateinit var SongLinks: Array<String?>

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
         * return true, otherwise false*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun networkChecker() {
        if (!isNetworkConnected()) {
            Toast.makeText(this@IncorrectSplash, "Please check your internet connection", Toast.LENGTH_LONG).show()
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    fun bulkwork(Level: String, Timed: Boolean) {
        val intent = Intent(this, MapsActivity()::class.java)
        /* pass the required parameters by intents for the MapsActivity to function */
        intent.putExtra("SONGLINKS", SongLinks) /* PASS ALL SONG LINKS*/
        intent.putExtra("SONGTITLES", SongTitles) /*PASS ALL SONG TITLES*/
        intent.putExtra("NUMBEROFSONGS", numberofsongs) /*PASS NUMBER OF SONGS*/
        intent.putExtra("Level", Level) /*Pass the level*/
        intent.putExtra("Timed", Timed) /*Pass if timed or not*/
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incorrect_splash)
        /*obtain the parameters from the MapsActivity, needed if the user wants to play again */
        SongTitles = intent.getStringArrayExtra("SONGTITLES")
        SongLinks = intent.getStringArrayExtra("SONGLINKS")
        numberofsongs = intent.getIntExtra("NUMBEROFSONGS", 1)

        val SONGYOUTUBELINK = intent.getStringExtra("SONGYOUTUBELINK")
        val SONGLYRICLINK = intent.getStringExtra("SONGLYRICLINK")
        val LEVEL = intent.getStringExtra("LEVEL") /*GET THE LEVEL NUMBER */
        if (LEVEL == 1.toString() || LEVEL == 2.toString()) {
            /*If the user has failed to guess the song correctly whilst playing insane or impossible
            * then we shall grey out the Youtube video button and the lyric link button
            * so the user visually knows they cannot use this button*/
            YOUTUBELINK2.setBackgroundResource(R.drawable.buttonsgreyed)
            LYRICLINK2.setBackgroundResource(R.drawable.buttonsgreyed)
        }

        HOMEBUTTON.setOnClickListener {
            networkChecker()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        YOUTUBELINK2.setOnClickListener {
            if (LEVEL == 1.toString() || LEVEL == 2.toString()) {
                /* If the user has been unsuccessful, at Insane or Impossible, they will not be allowed to view the Lyrics */
                Toast.makeText(this@IncorrectSplash, "Sorry, this is not available at this level", Toast.LENGTH_LONG).show()
            } else {
                if (isNetworkConnected()) {
                    /*If the user's connected to the internet*/
                    val url = "https://www.youtube.com/watch?v=" + (SONGYOUTUBELINK).drop(17)
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(this@IncorrectSplash, "No data connection, please investigate", Toast.LENGTH_LONG).show()
                }
            }
        }
        LYRICLINK2.setOnClickListener {
            if (LEVEL == 1.toString() || LEVEL == 2.toString()) {
                /* If the user has been unsuccessful, at Insane or Impossible, they will not be allowed to view the Lyrics */
                Toast.makeText(this@IncorrectSplash, "Sorry, this is not available at this level", Toast.LENGTH_LONG).show()
            } else {
                if (isNetworkConnected()) {
                    /*If the user's connected to the internet*/
                    val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$SONGLYRICLINK/words.txt"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(this@IncorrectSplash, "No data connection, please investigate", Toast.LENGTH_LONG).show()
                }

            }
        }
        SCORE_BUTTON2.setOnClickListener {
            val intent = Intent(this, ScoreScreen()::class.java)
            startActivity(intent)
        }
        SAMELEVEL2.setOnClickListener {
            networkChecker() /*Before continuing check the network status, change activity accordingly */
            alert("Want a challenge with timed play?") {
                positiveButton("Yes, bring it on!") {
                    bulkwork(LEVEL, true)
                }
                negativeButton("No Thanks!") {
                    bulkwork(LEVEL, false)
                }
                neutralButton("Exit") {
                    Toast.makeText(this@IncorrectSplash, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                }
            }.show()
        }
    }
}