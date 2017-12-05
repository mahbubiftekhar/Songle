package uk.co.iftekhar.www.songle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.content.Intent
import android.net.Uri


class SongLyricActivity : AppCompatActivity() {
    var SongNum = ""

    fun AddLyricsText(Title: String): String {
        /*function to add "-Lyrics" to a string and return that string */
        return """$Title - Lyrics"""
    }

    fun addButtonSongLyrics() {
        /* This will execute upon the async task being finnished */
        val NumberOfSongs_music = intent.getIntExtra("NUMBEROFSONGS", 1) /*Number of songs*/
        val SongTitles_music = intent.getStringArrayExtra("SONGTITLES")/*Array to store song titles */

        val ll_main: LinearLayout = findViewById(R.id.ll_main_layout) /*as LinearLayout */
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(1, 35, 1, 0) /* Used to set spaces between each button */
        var IndexCount = 0
        var SongNumber = 1
        for (i in 1..NumberOfSongs_music) {
            /*creates a button for each song, and says what the buttons do */
            val button_dynamic = Button(this)
            /* setting layout_width and layout_height using layout parameters */
            button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button_dynamic.text = AddLyricsText(SongTitles_music[IndexCount + 1])
            button_dynamic.setBackgroundResource(R.drawable.buttonxml)
            button_dynamic.layoutParams = params

            /*if the song number is single digit, we need to ensure that a 0 is in front e.g. 9 -> 09*/
            if (SongNumber < 10) {
                SongNum = "0" + (SongNumber).toString()
            } else {
                SongNum = (SongNumber).toString()
            }
            SongNumber += 1
            val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$SongNum/words.txt"
            button_dynamic.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            IndexCount++
            ll_main.addView(button_dynamic) /*Add each button*/
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        addButtonSongLyrics() /*Programmatically add the buttons*/
    }
}