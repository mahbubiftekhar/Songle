package uk.co.iftekhar.www.songle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView

class ScoreScreen : AppCompatActivity() {
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun LoadInt(key:String):Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getInt(key, 0)
        return savedValue
    }
    fun LoadLong(key:String):Long {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getLong(key, 0.toLong())
        return savedValue
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_screen)
        val a = LoadInt("EASY_LEVEL").toString()
        println("%% in Scorescreen" + a)
        val textView: TextView = findViewById(R.id.LEVEL1_SCORE)
        textView.text = a
        val B = LoadInt("NORMAL_LEVEL").toString()
        val textView2: TextView = findViewById(R.id.LEVEL2_SCORE)
        textView2.text = B
        val C = LoadInt("HARD_LEVEL").toString()
        val textView3: TextView = findViewById(R.id.LEVEL3_SCORE)
        textView3.text = C
        val D = LoadInt("INSANE_LEVEL").toString()
        val textView4: TextView = findViewById(R.id.LEVEL4_SCORE)
        textView4.text = D
        val E = LoadInt("INSANE_LEVEL").toString()
        val textView5: TextView = findViewById(R.id.LEVEL5_SCORE)
        textView5.text = E
        val TOTALSCORE = (a.toInt() + B.toInt()*2 + C.toInt()*3 + D.toInt()*4 + E.toInt()*5).toString()
        val textView6: TextView = findViewById(R.id.Score)
        textView6.text = TOTALSCORE
        ///// THE NEXT PART IS RETREIVING THE BEST TIMES FOR EACH

        val a1 = LoadLong("BEST_TIME_EASY").toString()
        val textView1: TextView = findViewById(R.id.LEVEL1_TIME)
        textView1.text = a1
        val B1 = LoadLong("BEST_TIME_NORMAL").toString()
        val textView21: TextView = findViewById(R.id.LEVEL2_TIME)
        textView21.text = B1
        val C1 = LoadLong("BEST_TIME_HARD").toString()
        val textView31: TextView = findViewById(R.id.LEVEL3_TIME)
        textView31.text = C1
        val D1 = LoadLong("BEST_TIME_INSANE").toString()
        val textView41: TextView = findViewById(R.id.LEVEL4_TIME)
        textView41.text = D1
        val E1 = LoadLong("BEST_TIME_IMPOSSIBLE").toString()
        val textView51: TextView = findViewById(R.id.LEVEL5_TIME)
        textView51.text = E1
    }
}
