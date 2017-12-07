package uk.co.iftekhar.www.songle

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView

class ScoreScreen : AppCompatActivity() {

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        return networkInfo != null && networkInfo.isConnected // 3
    }

    fun networkChecker() {
        /* function to check if network is available - using isNetworkConnected -
        if network is not available, the user will be taken to the network not available
        splash screen */
        if (!isNetworkConnected()) {
            //If there is a network issue send uesr to network issue page
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        networkChecker() /*check network status*/
        super.onBackPressed()
    }

    fun LoadInt(key: String): Int {
        /*Function to load an int shared preference*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getInt(key, 0)
        return savedValue
    }

    fun LoadLong(key: String): Long {
        /*Function to load an long shared preference*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getLong(key, 0.toLong())
        return savedValue
    }

    fun addHeader(text: String): String {
        /* Function to add "Number of successes" in front of the number of successes*/
        return "Number of successes: $text"
    }

    fun addBestTimeHeader(text: String): String {
        /* Function to add "Best Time: " in front of Time and "Secs behind it*/
        return "Best Time:  $text secs"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_screen)

        val EASY_LEVEL_SCORE = LoadInt("EASY_LEVEL").toString() /*Set easy level score*/
        val EASY_LEVEL_TEXTVIEW: TextView = findViewById(R.id.LEVEL1_SCORE)
        EASY_LEVEL_TEXTVIEW.text = addHeader(EASY_LEVEL_SCORE)

        val NORMAL_LEVEL_SCORE = LoadInt("NORMAL_LEVEL").toString() /*Set normal level score*/
        val NORMAL_LEVEL_TEXTVIEW: TextView = findViewById(R.id.LEVEL2_SCORE)
        NORMAL_LEVEL_TEXTVIEW.text = addHeader(NORMAL_LEVEL_SCORE)

        val HARD_LEVEL_SCORE = LoadInt("HARD_LEVEL").toString() /*Set hard level score*/
        val HARD_LEVEL_TEXTVIEW: TextView = findViewById(R.id.LEVEL3_SCORE)
        HARD_LEVEL_TEXTVIEW.text = addHeader(HARD_LEVEL_SCORE)

        val INSANE_LEVEL_SCORE = LoadInt("INSANE_LEVEL").toString() /*Set insane level score*/
        val INSANE_LEVEL_TEXTVIEW: TextView = findViewById(R.id.LEVEL4_SCORE)
        INSANE_LEVEL_TEXTVIEW.text = addHeader(INSANE_LEVEL_SCORE)

        val IMPOSSIBLE_LEVEL_SCORE = LoadInt("IMPOSSIBLE_LEVEL").toString() /*Set impossible level score*/
        val IMPOSSIBLE_LEVEL_TEXTVIEW: TextView = findViewById(R.id.LEVEL5_SCORE)
        IMPOSSIBLE_LEVEL_TEXTVIEW.text = addHeader(IMPOSSIBLE_LEVEL_SCORE)

        /*Calculate total score*/
        val TIMED_BONUS = LoadInt("TIMED_BONUS").toString() /*Set the timed bonus value*/
        val TIMED_LEVEL_TEXTVIEW: TextView = findViewById(R.id.TIMEDBONUSVALUE)
        TIMED_LEVEL_TEXTVIEW.text = "Timed Bonus: $TIMED_BONUS"

        val TOTAL_SCORE = (EASY_LEVEL_SCORE.toInt() + NORMAL_LEVEL_SCORE.toInt() * 2 + HARD_LEVEL_SCORE.toInt() * 3 + INSANE_LEVEL_SCORE.toInt() * 4 + IMPOSSIBLE_LEVEL_SCORE.toInt() * 5 + TIMED_BONUS.toInt()).toString()
        val TOTAL_SCORE_TEXTVIEW: TextView = findViewById(R.id.Score)
        TOTAL_SCORE_TEXTVIEW.text = TOTAL_SCORE /*set total score*/

        val BEST_TIME_EASY = LoadLong("BEST_TIME_EASY").toString() /*set easy level best time*/
        val BEST_EASY_TEXTVIEW: TextView = findViewById(R.id.LEVEL1_TIME)
        BEST_EASY_TEXTVIEW.text = addBestTimeHeader(BEST_TIME_EASY)

        val BEST_TIME_NORMAL = LoadLong("BEST_TIME_NORMAL").toString() /*set normal level best time*/
        val BEST_NORMAL_TEXTVIEW: TextView = findViewById(R.id.LEVEL2_TIME)
        BEST_NORMAL_TEXTVIEW.text = addBestTimeHeader(BEST_TIME_NORMAL)

        val BEST_TIME_HARD = LoadLong("BEST_TIME_HARD").toString() /*set hard level best time*/
        val BEST_HARD_TEXTVIEW: TextView = findViewById(R.id.LEVEL3_TIME)
        BEST_HARD_TEXTVIEW.text = addBestTimeHeader(BEST_TIME_HARD)

        val BEST_TIME_INSANE = LoadLong("BEST_TIME_INSANE").toString() /*set insane level best time*/
        val BEST_INSANE_TEXTVIEW: TextView = findViewById(R.id.LEVEL4_TIME)
        BEST_INSANE_TEXTVIEW.text = addBestTimeHeader(BEST_TIME_INSANE)

        val BEST_TIME_IMPOSSIBLE = LoadLong("BEST_TIME_IMPOSSIBLE").toString() /*set impossible level best time*/
        val BEST_IMPOSSIBLE_TEXTVIEW: TextView = findViewById(R.id.LEVEL5_TIME)
        BEST_IMPOSSIBLE_TEXTVIEW.text = addBestTimeHeader(BEST_TIME_IMPOSSIBLE)
    }
}
