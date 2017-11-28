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
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        return networkInfo != null && networkInfo.isConnected // 3
    }

    fun networkChecker (){
        if(!isNetworkConnected()){
            Toast.makeText(this@IncorrectSplash, "Please check your internet connection", Toast.LENGTH_LONG).show()
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    fun bulkwork(Level: String, Timed: Boolean){
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

        /*obtain the parameters from the MapsActivity, needed if the user wants to play
        again
         */
        SongTitles = intent.getStringArrayExtra("SONGTITLES")
        SongLinks = intent.getStringArrayExtra("SONGLINKS")
        numberofsongs = intent.getIntExtra("NUMBEROFSONGS",1)

        val SONGYOUTUBELINK = intent.getStringExtra("SONGYOUTUBELINK")
        val SONGLYRICLINK = intent.getStringExtra("SONGLYRICLINK")
        val LEVEL = intent.getStringExtra("LEVEL") /*GET THE LEVEL NUMBER */
        YOUTUBELINK.setOnClickListener {
            if(isNetworkConnected()) {
            val url = "https://www.youtube.com/watch?v=" + (SONGYOUTUBELINK).drop(17)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } else {
                Toast.makeText(this@IncorrectSplash, "No data connection, please investigate", Toast.LENGTH_LONG).show()
            }
        }
        LYRICLINK.setOnClickListener {
            if(isNetworkConnected()) {
            val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$SONGLYRICLINK/words.txt"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } else {
                Toast.makeText(this@IncorrectSplash, "No data connection, please investigate", Toast.LENGTH_LONG).show()
            }
        }
        SCORE_BUTTON.setOnClickListener {
            val intent = Intent(this, ScoreScreen()::class.java)
            startActivity(intent)
        }
        SAMELEVEL.setOnClickListener {
            networkChecker() /*Before continuing check the network status, change activity accordingly */
            alert("Want a challenge with timed play?") {
                positiveButton("Yes, bring it on!") {
                    bulkwork(LEVEL, true)
                }
                negativeButton("No Thanks!") {
                    bulkwork(LEVEL, false)
                }
                neutralButton("Exit"){
                    Toast.makeText(this@IncorrectSplash, "Read the FAQ if you need help", Toast.LENGTH_SHORT).show()
                }
            }.show()
        }
    }
}