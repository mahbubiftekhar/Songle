package uk.co.iftekhar.www.songle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.content.Intent
import android.net.Uri
import android.view.View
import kotlinx.android.synthetic.main.activity_music.*

class MusicActivity : AppCompatActivity() {
    fun addButtons() {
        val NumberOfSongs_music = intent.getIntExtra("NUMBEROFSONGS", 1) /*Number of songs*/
        val SongTitles_music = intent.getStringArrayExtra("SONGTITLES")/*Array to store song titles */
        val SongLinks_music = intent.getStringArrayExtra("SONGLINKS") /*Array to store links titles */

        val ll_main: LinearLayout = findViewById(R.id.ll_main_layout); //as LinearLayout
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(1, 35, 1, 0) /* Used to set spaces between each button */
        for (i in 1..NumberOfSongs_music) { /*creates enough buttons for each song, and says what the buttons do */
            val button_dynamic = Button(this)
            /* setting layout_width and layout_height using layout parameters */
            button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button_dynamic.text = SongTitles_music[i]
            button_dynamic.layoutParams = params
            button_dynamic.setBackgroundResource(R.drawable.buttonxml)

            val url = "https://www.youtube.com/watch?v=" + (SongLinks_music[i - 1])!!.drop(17)
            button_dynamic.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            ll_main.addView(button_dynamic)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        addButtons()
    }
}