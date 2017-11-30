package uk.co.iftekhar.www.songle

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

    override fun onBackPressed() {
        networkChecker() /*check network status*/
        super.onBackPressed()
    }

    fun LoadInt(key: String): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getInt(key, 0)
        return savedValue
    }

    fun LoadLong(key: String): Long {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getLong(key, 0.toLong())
        return savedValue
    }

    fun getText(text: String): String {
        /* Function to add "Number of successes" in front of the number of successes*/
        return "Number of successes: $text"
    }

    fun getText2(text: String): String {
        /* Function to add "Best Time: " in front of Time and "Secs behind it*/
        return "Best Time:  $text secs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_screen)

        val a = LoadInt("EASY_LEVEL").toString() /*Set easy level score*/
        val textView: TextView = findViewById(R.id.LEVEL1_SCORE)
        textView.text = getText(a)

        val B = LoadInt("NORMAL_LEVEL").toString() /*Set normal level score*/
        val textView2: TextView = findViewById(R.id.LEVEL2_SCORE)
        textView2.text = getText(B)

        val C = LoadInt("HARD_LEVEL").toString() /*Set hard level score*/
        val textView3: TextView = findViewById(R.id.LEVEL3_SCORE)
        textView3.text = getText(C)

        val D = LoadInt("INSANE_LEVEL").toString() /*Set insane level score*/
        val textView4: TextView = findViewById(R.id.LEVEL4_SCORE)
        textView4.text = getText(D)

        val E = LoadInt("IMPOSSIBLE_LEVEL").toString() /*Set impossible level score*/
        val textView5: TextView = findViewById(R.id.LEVEL5_SCORE)
        textView5.text = getText(E)

        /*Calculate total score*/
        val TOTALSCORE = (a.toInt() + B.toInt() * 2 + C.toInt() * 3 + D.toInt() * 4 + E.toInt() * 5).toString()
        val textView6: TextView = findViewById(R.id.Score)
        textView6.text = TOTALSCORE /*set total score*/

        val a1 = LoadLong("BEST_TIME_EASY").toString() /*set easy level best time*/
        val textView1: TextView = findViewById(R.id.LEVEL1_TIME)
        textView1.text = getText2(a1)

        val B1 = LoadLong("BEST_TIME_NORMAL").toString() /*set normal level best time*/
        val textView21: TextView = findViewById(R.id.LEVEL2_TIME)
        textView21.text = getText2(B1)

        val C1 = LoadLong("BEST_TIME_HARD").toString() /*set hard level best time*/
        val textView31: TextView = findViewById(R.id.LEVEL3_TIME)
        textView31.text = getText2(C1)

        val D1 = LoadLong("BEST_TIME_INSANE").toString() /*set insane level best time*/
        val textView41: TextView = findViewById(R.id.LEVEL4_TIME)
        textView41.text = getText2(D1)

        val E1 = LoadLong("BEST_TIME_IMPOSSIBLE").toString() /*set impossible level best time*/
        val textView51: TextView = findViewById(R.id.LEVEL5_TIME)
        textView51.text = getText2(E1)
    }
}
