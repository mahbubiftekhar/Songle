package uk.co.iftekhar.www.songle

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_correct_splash.*
import org.jetbrains.anko.alert

class CorrectSplash : AppCompatActivity() {
    var numberofsongs = 0
    lateinit var SongTitles: Array<String?>
    lateinit var SongLinks: Array<String?>

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        super.onBackPressed() Send the user back to MainActivity */
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun isNetworkConnected(): Boolean {
        /*function get network status*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun networkChecker() {
        /*If there is not a network then we shall change the activity */
        if (!isNetworkConnected()) {
            Toast.makeText(this@CorrectSplash, "Check your internet connection", Toast.LENGTH_LONG).show()
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }

    }

    fun bulkWork(Level: String, Timed: Boolean) {
        val intent = Intent(this, MapsActivity()::class.java)
        intent.putExtra("SONGLINKS", SongLinks) /* PASS ALL SONG LINKS*/
        intent.putExtra("SONGTITLES", SongTitles) /*PASS ALL SONG TITLES*/
        intent.putExtra("NUMBEROFSONGS", numberofsongs) /*PASS NUMBER OF SONGS*/
        intent.putExtra("Level", Level) /*Pass the level*/
        intent.putExtra("Timed", Timed) /*Pass if timed or not*/
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correct_splash)
        SongTitles = intent.getStringArrayExtra("SONGTITLES")
        SongLinks = intent.getStringArrayExtra("SONGLINKS")
        numberofsongs = intent.getIntExtra("NUMBEROFSONGS", 1) /*Number of songs in XML, please note 0 is the start */

        /*get the song youtube link from MapsActivity */
        val SONGYOUTUBELINK = intent.getStringExtra("SONGYOUTUBELINK")
        /*get the song lyric link from MapsActivity */
        val SONGLYRICLINK = intent.getStringExtra("SONGLYRICLINK")
        val LEVEL = intent.getStringExtra("LEVEL") /*GET THE LEVEL NUMBER */

        /*on click listeners for the different buttons */
        YOUTUBELINK.setOnClickListener {
            if(isNetworkConnected()){ /* Check the nework status before continuing
            the reason I do not use the networkChecker() is that I want the user to be able to quickly fix the
            issue, as if I take then to the networkIssue splash when they don't have a network, they can not review the
            song afterwards by pressing back - as the back button on NetworkIssue is override to take the user back to main screen
            only
            */
            val url = "https://www.youtube.com/watch?v=" + (SONGYOUTUBELINK).drop(17)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } else {
                Toast.makeText(this@CorrectSplash, "No data connection, please investigate", Toast.LENGTH_LONG).show()
            }
        }
        LYRICLINK.setOnClickListener {
            if(isNetworkConnected())   {
            val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$SONGLYRICLINK/words.txt"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } else {
                Toast.makeText(this@CorrectSplash, "No data connection, please investigate", Toast.LENGTH_LONG).show()
            }
        }
        SAMELEVEL.setOnClickListener {
            networkChecker() /* Check the nework status before continuing*/
            alert("Want a challenge with timed play?") {
                /* ask user if they wish to play timed*/
                positiveButton("Yes, bring it on!") {
                    bulkWork(LEVEL, true)
                }
                negativeButton("No Thanks!") {
                    bulkWork(LEVEL, false)


                }
            }.show()
        }
        SCORE_BUTTON.setOnClickListener {
            val intent = Intent(this, ScoreScreen()::class.java)
            startActivity(intent)
        }
        NEXTLEVEL.setOnClickListener {
            networkChecker()
            if (LEVEL == "5") {
                /* if the users prevous level
                was level 5, we simply let the user redo that level
                 */
                alert("Want a challenge with timed play?") {
                    /* ask user if they wish to play timed*/
                    positiveButton("Yes, bring it on!") {
                        bulkWork("5", true)

                    }
                    negativeButton("No Thanks!") {
                        bulkWork("5", false)
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@CorrectSplash, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            } else if (LEVEL == "4") {
                alert("Want a challenge with timed play?") {
                    /* ask user if they wish to play timed*/
                    positiveButton("Yes, bring it on!") {
                        bulkWork("5", true)
                    }
                    negativeButton("No Thanks!") {
                        bulkWork("5", false)
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@CorrectSplash, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            } else if (LEVEL == "3") {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkWork("4", true)

                    }
                    negativeButton("No Thanks!") {
                        bulkWork("4", false)
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@CorrectSplash, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            } else if (LEVEL == "2") {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkWork("3", true)
                    }
                    negativeButton("No Thanks!") {
                        bulkWork("3", false)
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@CorrectSplash, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()

            } else {
                alert("Want a challenge with timed play?") {
                    positiveButton("Yes, bring it on!") {
                        bulkWork("2", true)
                    }
                    negativeButton("No Thanks!") {
                        bulkWork("2", false)
                    }
                    neutralButton("Exit") {
                        Toast.makeText(this@CorrectSplash, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                    }
                }.show()
            }
        }

    }
}
